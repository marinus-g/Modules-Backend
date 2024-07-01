package academy.mischok.modules.service;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Optional;

public interface OAuthService {

    Optional<OidcUser> findUserByToken(String token);

}
