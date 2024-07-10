package academy.mischok.modules.service.impl;

import academy.mischok.modules.service.OauthClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthClientServiceImpl implements OauthClientService {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public OAuth2AccessToken getAccessToken(OAuth2AuthenticationToken authentication) {
        if (authentication == null) {
            throw new IllegalStateException("No authentication found");
        }
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        if (client == null) {
            throw new IllegalStateException(authorizedClientService.getClass().getName() + " No authorized client found " + authentication.getAuthorizedClientRegistrationId() + " " + authentication.getName());
        }
        return client.getAccessToken();
    }

    static HttpEntity<String> buildHttpEntity(OauthClientService oAuthClientService) {
        final OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        OAuth2AccessToken accessToken = oAuthClientService.getAccessToken(authentication);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getTokenValue());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }
}
