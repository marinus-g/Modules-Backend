package academy.mischok.modules.service.impl;

import academy.mischok.modules.service.OauthClientService;
import academy.mischok.modules.service.UserService;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String USER_ENDPOINT = "/v1.0/users";
    private static final String ID_ENDPOINT = USER_ENDPOINT + "/%s?$select=id";

    private final OauthClientService oAuthClientService;
    private final RestTemplate restTemplate;

    @Value("${microsoft.graph-url}")
    private String graphUrl;

    // in theory, we should use a loading cache (for example Caffeine) for the uuids & existsByEmail

    @Override
    public boolean existsByEmail(String email) {
        HttpEntity<String> entity = OAuthClientServiceImpl.buildHttpEntity(oAuthClientService);
        ResponseEntity<String> response = restTemplate.exchange(graphUrl + String.format(ID_ENDPOINT, email), HttpMethod.GET, entity, String.class);
        return response.getStatusCode() == HttpStatus.OK;
    }



    @Override
    public Optional<UUID> findIdByEmail(String email) {
        HttpEntity<String> entity = OAuthClientServiceImpl.buildHttpEntity(oAuthClientService);
            return Optional
                    .of(restTemplate.exchange(graphUrl + String.format(ID_ENDPOINT, email), HttpMethod.GET, entity, String.class))
                    .filter(response -> response.getStatusCode() == HttpStatus.OK)
                    .map(HttpEntity::getBody)
                    .map(JsonParser::parseString)
                    .map(JsonElement::getAsJsonObject)
                    .filter(jsonObject -> jsonObject.has("id"))
                    .map(jsonObject -> jsonObject.get("id").getAsString())
                    .map(UUID::fromString);
        }
}
