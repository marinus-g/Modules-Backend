package academy.mischok.modules.controller;

import academy.mischok.modules.configuration.OAuth2ClientConfiguration;
import academy.mischok.modules.util.OAuth2TestUtil;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@Import(OAuth2ClientConfiguration.class)
public abstract class AbstractControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected OAuth2AuthorizedClientService authorizedClientService;

    protected OAuth2AuthenticationToken authenticationToken;

    @BeforeAll
    public static void beforeAll() {
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8081).httpsPort(8443));
        wireMockServer.start();
        configureFor("localhost", 8081);
        stubFor(WireMock.get(urlEqualTo("/v1.0/groups"))
                //.withHost(equalTo("graph.microsoft.com"))
                .willReturn(aResponse()
                       // .proxiedFrom("https://graph.microsoft.com")
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("groups.json")));
    }

    protected void mockAuthClient() {
        // Mock OidcUser
        OidcUser oidcUser = OAuth2TestUtil.createMockOidcUser();

        // Mock OAuth2AuthenticationToken
        authenticationToken = new OAuth2AuthenticationToken(
                oidcUser,
                oidcUser.getAuthorities(),
                "random-id"
        );

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "access-token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        // Mock OAuth2AuthorizedClient
        OAuth2AuthorizedClient mockClient = mock(OAuth2AuthorizedClient.class);
        when(mockClient.getAccessToken()).thenReturn(accessToken);
        // Mock OAuth2AuthorizedClient
        when(authorizedClientService.loadAuthorizedClient("random-id", "random-id")).thenReturn(mockClient);
    }

    @AfterAll
    public static void afterAll() {
        WireMock.shutdownServer();
    }

}
