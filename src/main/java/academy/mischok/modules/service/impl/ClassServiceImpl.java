package academy.mischok.modules.service.impl;

import academy.mischok.modules.model.SchoolClass;
import academy.mischok.modules.repository.SchoolClassRepository;
import academy.mischok.modules.service.ClassService;
import academy.mischok.modules.service.OauthClientService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private static final String GROUPS_ENDPOINT = "/v1.0/groups";

    @Value("${microsoft.graph-url}")
    private String graphUrl;

    private final OauthClientService oAuthClientService;
    private final RestTemplate restTemplate;
    private final SchoolClassRepository repository;
    private long lastFetch = 0;

    public List<SchoolClass> findClasses(OAuth2AuthenticationToken authentication) {
        fetchClasses();
        return repository.findAll();
    }

    @Override
    public Optional<SchoolClass> findClassByName(String name) {
        fetchClasses();
        return repository.findByClassId(name);
    }
    private void fetchClasses() {
        if (lastFetch + 1000 * 60 * 60 > System.currentTimeMillis()) {
            return;
        }
        final OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        OAuth2AccessToken accessToken = oAuthClientService.getAccessToken(authentication);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getTokenValue());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(graphUrl + GROUPS_ENDPOINT, HttpMethod.GET, entity, String.class);
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new IllegalStateException("Failed to fetch classes");
        }
        System.out.println("ASDFASDF");

        JsonObject object = JsonParser.parseString(response.getBody()).getAsJsonObject();
        lastFetch = System.currentTimeMillis();
        object.get("value").getAsJsonArray().asList().stream()
                .map(JsonElement::getAsJsonObject)
                .map(jsonObject
                        -> new SchoolClass(
                        jsonObject.get("id").getAsString(),
                        jsonObject.get("displayName").getAsString())
                )
                .filter(schoolClass -> schoolClass.getName().startsWith("U") && schoolClass.getName().endsWith("UFI"))
                .forEach(schoolClass -> {
                    repository.findByClassId(schoolClass.getClassId()).ifPresentOrElse(schoolClass1 -> {
                        schoolClass1.setName(schoolClass.getName());
                        repository.save(schoolClass1);
                    }, () -> {
                        schoolClass.setModules(new ArrayList<>());
                        repository.save(schoolClass);
                    });
                });
        for (SchoolClass schoolClass : repository.findAll()) {
            System.out.println(schoolClass.getName());
        }
    }
}