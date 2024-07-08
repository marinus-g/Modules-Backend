package academy.mischok.modules.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class MicrosoftGraphService {

    private static final String MICROSOFT_GRAPH_API_URL = "https://graph.microsoft.com/v1.0";
    private RestTemplate restTemplate;

    public MicrosoftGraphService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public String getUserId(String accessToken) {
        String url = MICROSOFT_GRAPH_API_URL + "/me";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody();
    }

    public List<String> getGroupIds(String accessToken){
        String url = MICROSOFT_GRAPH_API_URL + "/me/memberOf";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return Arrays.asList(response.getBody().split(","));
    }
}
