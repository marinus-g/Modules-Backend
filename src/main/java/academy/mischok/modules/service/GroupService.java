package academy.mischok.modules.service;

import academy.mischok.modules.model.Group;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service

public class GroupService {

    private static final String GROUPS_ENDPOINT = "https://graph.microsoft.com/v1.0/groups";

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    public GroupService(OAuth2AuthorizedClientService authorizedClientService, ClientRegistrationRepository clientRegistrationRepository) {
        this.authorizedClientService = authorizedClientService;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    public void test(OAuth2AuthenticationToken authentication) {

        if (authentication == null) {
            throw new IllegalStateException("No authentication found");
        }
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        if (client == null) {
            System.out.println("No authorized client found for '" + authentication.getAuthorizedClientRegistrationId() + "' and user '" + authentication.getName() + "'");
            throw new IllegalStateException("No authorized client found");
        }
        OAuth2AccessToken accessToken = client.getAccessToken();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getTokenValue());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(GROUPS_ENDPOINT, HttpMethod.GET, entity, String.class);

        JsonObject object = JsonParser.parseString(response.getBody()).getAsJsonObject();
        object.get("value").getAsJsonArray().asList().stream()
                        .map(JsonElement::getAsJsonObject)
                                .map(jsonObject -> new Group(jsonObject.get("id").getAsString(), jsonObject.get("displayName").getAsString()))
                                        .filter(group -> group.getDisplayName().endsWith("UFI"))
                                                .forEach(group -> System.out.println(group.getDisplayName() + " - " + group.getId()));

    }

}
