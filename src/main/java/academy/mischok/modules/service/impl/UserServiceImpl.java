package academy.mischok.modules.service.impl;

import academy.mischok.modules.model.OAuthUser;
import academy.mischok.modules.service.OauthClientService;
import academy.mischok.modules.service.UserService;
import academy.mischok.modules.service.impl.cache.UserServiceCache;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String USER_OF_GROUP_ENDPOINT = "/v1.0/groups/%s/members?$select=id,mail";

    private final OauthClientService oAuthClientService;
    private final RestTemplate restTemplate;

    @Value("${microsoft.graph-url}")
    private String graphUrl;
    private final UserServiceCache userServiceCache;

    // in theory, we should use a loading cache (for example Caffeine) for the uuids & existsByEmail

    @Override
    public boolean existsByEmail(UUID group, String email) {
        HttpEntity<String> entity = OAuthClientServiceImpl.buildHttpEntity(oAuthClientService);
        ResponseEntity<String> response = restTemplate.exchange(graphUrl + String.format(USER_OF_GROUP_ENDPOINT, group), HttpMethod.GET, entity, String.class);
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            return false;
        }
        return Optional
                .of(JsonParser.parseString(response.getBody()))
                .map(JsonElement::getAsJsonObject)
                .filter(jsonObject -> jsonObject.has("value"))
                .map(jsonObject -> jsonObject.getAsJsonArray("value"))
                .map(JsonElement::getAsJsonArray)
                .map(jsonArray -> jsonArray
                        .asList()
                        .stream()
                        .map(JsonElement::getAsJsonObject)
                        .filter(jsonObject -> jsonObject.has("mail"))
                        .map(jsonObject -> jsonObject.get("mail").getAsString())
                        .anyMatch(id -> id.equalsIgnoreCase(email)))
                .orElse(false);
    }


    @Override
    public Optional<UUID> findIdByEmail(UUID group, String email) {
        try {
            HttpEntity<String> entity = OAuthClientServiceImpl.buildHttpEntity(oAuthClientService);
            return Optional
                    .of(restTemplate.exchange(graphUrl + String.format(USER_OF_GROUP_ENDPOINT, group), HttpMethod.GET, entity, String.class))
                    .filter(response -> response.getStatusCode() == HttpStatus.OK)
                    .map(HttpEntity::getBody)
                    .map(s -> {
                        log.info("response: {}", s);
                        return s;
                    })
                    .map(JsonParser::parseString)
                    .map(JsonElement::getAsJsonObject)
                    .filter(jsonObject -> jsonObject.has("value"))
                    .map(jsonObject -> jsonObject.getAsJsonArray("value"))
                    .map(JsonElement::getAsJsonArray)
                    .map(jsonArray -> jsonArray
                            .asList()
                            .stream()
                            .map(JsonElement::getAsJsonObject)
                            .filter(jsonObject -> jsonObject.has("mail"))
                            .filter(jsonObject -> jsonObject.get("mail").getAsString().equalsIgnoreCase(email))
                            .peek(jsonObject -> log.info("!!found {}", jsonObject))
                            .map(jsonObject -> jsonObject.get("id").getAsString())
                            .map(UUID::fromString)
                            .findFirst()
                    )
                    .filter(Optional::isPresent)
                    .flatMap(uuid -> uuid);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<OAuthUser> findUserById(UUID userId) {
        return this.userServiceCache.findUserById(userId);
    }

    @Override
    public List<OAuthUser> findUsersInGroup(UUID group) {
        return this.userServiceCache.findUsersInGroup(group);
    }
}
