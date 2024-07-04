package academy.mischok.modules.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(httpSecurityCorsConfigurer ->
                        httpSecurityCorsConfigurer.configurationSource(request -> {
                            CorsConfiguration configuration = new CorsConfiguration();
                            configuration.setAllowedOriginPatterns(List.of("*"));
                         //   configuration.setAllowedOrigins(List.of("http://localhost:4200"));
                            configuration.setAllowCredentials(true);
                            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "OPTIONS"));
                            configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

                            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                            source.registerCorsConfiguration("/**", configuration.applyPermitDefaultValues());
                            return configuration;
                        }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/error", "/oauth2/**").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.oidcUserService(oidcUserService()))
                                .defaultSuccessUrl("https://academy-u202309-031-2febaeeb9a88.herokuapp.com/login/oauth2/code", true) // Redirect to Angular app

                )
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/"));
        return http.build();
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        return userRequest -> {
            OidcUser oidcUser = new OidcUserService().loadUser(userRequest);
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            oidcUser.getAuthorities().forEach(authority -> {
                if (authority instanceof OidcUserAuthority oidcUserAuthority) {
                    OidcIdToken idToken = oidcUserAuthority.getIdToken();
                    idToken.getClaims().forEach((s, o) -> System.out.println(s + " - " + o));
                    List<String> roles = idToken.getClaimAsStringList("roles");
                    if (roles != null) {
                        roles.forEach(role -> mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
                    }
                }
            });
            return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
        };
    }
}
