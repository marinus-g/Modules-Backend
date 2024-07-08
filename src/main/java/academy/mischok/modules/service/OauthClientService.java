package academy.mischok.modules.service;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

public interface OauthClientService {

    OAuth2AccessToken getAccessToken(OAuth2AuthenticationToken authentication);

}