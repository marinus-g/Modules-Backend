package academy.mischok.modules.util;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class OAuth2TestUtil {


    public static RequestPostProcessor mockOidcLogin() {
        // Define claims for the ID token
        Map<String, Object> claims = Map.of(
                "sub", "random-id",
                "name", "John Doe",
                "email", "johndoe@example.com"
        );

        // Create a mock ID token with claims
        OidcIdToken idToken = new OidcIdToken(
                "tokenValue",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                claims
        );

        // Create a mock principal with authorities and ID token
        OidcUser principal = new DefaultOidcUser(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                idToken
        );

        // Create an OAuth2AuthenticationToken with the mock principal
        OAuth2AuthenticationToken authenticationToken = new OAuth2AuthenticationToken(
                principal,
                principal.getAuthorities(),
                "registrationId"
        );
        return SecurityMockMvcRequestPostProcessors.authentication(authenticationToken);
    }

}
