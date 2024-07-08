package academy.mischok.modules.service.impl;

import academy.mischok.modules.model.SchoolClass;
import academy.mischok.modules.repository.SchoolClassRepository;
import academy.mischok.modules.service.ClassService;
import academy.mischok.modules.service.OauthClientService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private static final String GROUPS_ENDPOINT = "https://graph.microsoft.com/v1.0/groups";

    private final OauthClientService oAuthClientService;
    private final RestTemplate restTemplate;
    private final SchoolClassRepository repository;
    private long lastFetch = 0;

    public List<SchoolClass> findClasses(OAuth2AuthenticationToken authentication) {

        if (lastFetch + 1000 * 60 * 60 > System.currentTimeMillis()) {
            return this.repository.findAll();
        }
        OAuth2AccessToken accessToken = oAuthClientService.getAccessToken(authentication);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getTokenValue());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(GROUPS_ENDPOINT, HttpMethod.GET, entity, String.class);
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new IllegalStateException("Failed to fetch classes");
        }
        JsonObject object = JsonParser.parseString(response.getBody()).getAsJsonObject();
        lastFetch = System.currentTimeMillis();
        return object.get("value").getAsJsonArray().asList().stream()
                .map(JsonElement::getAsJsonObject)
                .map(jsonObject
                        -> new SchoolClass(
                        jsonObject.get("id").getAsString(),
                        jsonObject.get("displayName").getAsString())
                )
                .filter(schoolClass -> schoolClass.getName().startsWith("U") && schoolClass.getName().endsWith("UFI"))
                .map(schoolClass -> {
                    return repository.findByClassId(schoolClass.getClassId()).orElseGet(() -> repository.save(schoolClass));
                })
                .toList();
    }
}