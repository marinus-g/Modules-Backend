package academy.mischok.modules.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

//@Configuration
public class WebSecurityConfiguration {

    private final String redirectUrl;

    public WebSecurityConfiguration(
            @Value("${spring.cloud.azure.active-directory.redirect-uri-template}")
            String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return
                http
                        .cors(configurer -> {
                            configurer.configurationSource(request -> {
                                var cors = new org.springframework.web.cors.CorsConfiguration();
                                cors.setAllowedOrigins(List.of("*"));
                                cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                                return cors;
                            });
                        })
                        .oauth2Login(configurer ->  {
                            configurer
                                    .loginPage("/oauth2/authorization/microsoft")
                                    .defaultSuccessUrl(redirectUrl, false);
                        })
                        .authorizeHttpRequests(configurer -> {
                            configurer
                                    .requestMatchers("/oauth2/authorization/microsoft", "/error").permitAll()
                                    .anyRequest().authenticated();

                        })
                        .build();
    }
}
