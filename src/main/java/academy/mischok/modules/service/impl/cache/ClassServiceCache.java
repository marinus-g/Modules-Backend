package academy.mischok.modules.service.impl.cache;

import academy.mischok.modules.model.SchoolClass;
import academy.mischok.modules.service.OauthClientService;
import academy.mischok.modules.service.impl.OAuthClientServiceImpl;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Component
public class ClassServiceCache {

    private static final String GROUPS_ENDPOINT = "/v1.0/groups";

    private final RestTemplate restTemplate;
    private final String graphUrl;
    private final OauthClientService oAuthClientService;

    @Autowired
    public ClassServiceCache(RestTemplate restTemplate, @Value("${microsoft.graph-url}") String graphUrl,
                             OauthClientService oAuthClientService) {
        this.restTemplate = restTemplate;
        this.graphUrl = graphUrl;
        this.oAuthClientService = oAuthClientService;
    }

    @Cacheable("fetchClasses")
    public List<SchoolClass> fetchClasses() {
        HttpEntity<String> entity = OAuthClientServiceImpl.buildHttpEntity(oAuthClientService);
        ResponseEntity<String> response = restTemplate.exchange(graphUrl + GROUPS_ENDPOINT, HttpMethod.GET, entity, String.class);
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new IllegalStateException("Failed to fetch classes");
        }
        JsonObject object = JsonParser.parseString(response.getBody()).getAsJsonObject();

        return object.get("value").getAsJsonArray().asList().stream()
                .map(JsonElement::getAsJsonObject)
                .map(jsonObject
                        -> new SchoolClass(
                        UUID.fromString(jsonObject.get("id").getAsString()),
                        jsonObject.get("displayName").getAsString())
                )
                .filter(schoolClass -> schoolClass.getName().startsWith("U") && schoolClass.getName().endsWith("UFI"))
                .toList();
    }

}
