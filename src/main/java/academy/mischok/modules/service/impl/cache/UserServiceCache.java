package academy.mischok.modules.service.impl.cache;

import academy.mischok.modules.model.OAuthUser;
import academy.mischok.modules.service.OauthClientService;
import academy.mischok.modules.service.impl.OAuthClientServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserServiceCache {

    private static final String USER_ENDPOINT = "/v1.0/users/%s?$select=surname,givenName,mail, id";

    private final OauthClientService oAuthClientService;
    private final RestTemplate restTemplate;
    @Value("${microsoft.graph-url}")
    private String graphUrl;

    @Cacheable("findUserById")
    public Optional<OAuthUser> findUserById(UUID userId) {
        HttpEntity<String> entity = OAuthClientServiceImpl.buildHttpEntity(oAuthClientService);
        ResponseEntity<OAuthUser> response = restTemplate.exchange(
                graphUrl + String.format(USER_ENDPOINT, userId.toString()),
                HttpMethod.GET, entity, OAuthUser.class);
        return Optional.of(response)
                .filter(oAuthUserResponseEntity -> oAuthUserResponseEntity.getStatusCode().is2xxSuccessful())
                .map(ResponseEntity::getBody);
    }
}
