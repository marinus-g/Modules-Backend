package academy.mischok.modules.controller;

import academy.mischok.modules.service.UserService;
import academy.mischok.modules.util.OAuth2TestUtil;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(httpsEnabled = true, httpsPort = 0, proxyMode = true)
class ExamControllerTest extends AbstractControllerTest {

    @Autowired
    private FilterChainProxy filterChainProxy;

    @Test
    void testUploadExam() throws Exception {
        super.mockAuthClient();
        String contentAsString = mockMvc.perform(get("/class/me")
                        .with(OAuth2TestUtil.mockOidcLogin())
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonObject meClassJson = JsonParser.parseString(contentAsString).getAsJsonObject();
        assert meClassJson.has("id");
        final UUID classId = UUID.fromString(meClassJson.get("id").getAsString());
        // upload exam
        // append the file present in src/test/resources/test.xlsx

        final Map<String, UUID> map = mockMethods();

        final String moduleData = """
                {
                    "name": "fileUploadTest1",
                    "description": "fileUploadTest1"
                }
                """;
        String moduleLocation = mockMvc.perform(post("/module")
                        .content(moduleData)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(OAuth2TestUtil.mockLecturerOidcLogin())
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/module/")))
                .andReturn()
                .getResponse()
                .getHeader("Location");
        final Long moduleId = Long.parseLong(moduleLocation.substring(moduleLocation.lastIndexOf("/") + 1));
        mockMvc.perform(post("/class/%s/module/%s".formatted(classId, moduleId))
                        .with(OAuth2TestUtil.mockLecturerOidcLogin())
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        final String classModuleLocation = mockMvc.perform(get(String.format("/class/%s/module/%s", classId, moduleId))
                        .with(OAuth2TestUtil.mockLecturerOidcLogin())
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonObject classModuleJson = JsonParser.parseString(classModuleLocation).getAsJsonObject();
        assert classModuleJson.has("id");
        final long classModuleId = classModuleJson.get("id").getAsLong();

        MockMultipartFile file = getTestFile();
        String location = mockMvc.perform(multipart(String.format("/exam/module/%s", classModuleId))
                        .file(file)
                        .with(OAuth2TestUtil.mockLecturerOidcLogin())
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/exam/")))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        assert location != null;
        mockMvc.perform(get(location)
                        .with(OAuth2TestUtil.mockLecturerOidcLogin())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.class_id").value(classId.toString()))
                .andExpect(jsonPath("$.module_id").value(moduleId))
                .andExpect(jsonPath("$.max_score").isNumber())
                .andExpect(jsonPath("$.max_score", is(41)))
                .andExpect(jsonPath("$.date").isString())
                .andExpect(jsonPath("$.date", is("2023-12-22 00:00:00")))
                .andExpect(jsonPath("$.exam_results").isArray());

    }

    @Test
    void testGetOwnExam() throws Exception {
        super.mockAuthClient();
        String contentAsString = mockMvc.perform(get("/class/me")
                        .with(OAuth2TestUtil.mockOidcLogin())
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonObject meClassJson = JsonParser.parseString(contentAsString).getAsJsonObject();
        assert meClassJson.has("id");
        final UUID classId = UUID.fromString(meClassJson.get("id").getAsString());
        // upload exam
        // append the file present in src/test/resources/test.xlsx

        final Map<String, UUID> map = mockMethods();

        final String moduleData = """
                {
                    "name": "fileUploadTest2",
                    "description": "fileUploadTest2"
                }
                """;
        String moduleLocation = mockMvc.perform(post("/module")
                        .content(moduleData)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(OAuth2TestUtil.mockLecturerOidcLogin())
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/module/")))
                .andReturn()
                .getResponse()
                .getHeader("Location");
        final Long moduleId = Long.parseLong(moduleLocation.substring(moduleLocation.lastIndexOf("/") + 1));
        mockMvc.perform(post("/class/%s/module/%s".formatted(classId, moduleId))
                        .with(OAuth2TestUtil.mockLecturerOidcLogin())
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        final String classModuleLocation = mockMvc.perform(get(String.format("/class/%s/module/%s", classId, moduleId))
                        .with(OAuth2TestUtil.mockLecturerOidcLogin())
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonObject classModuleJson = JsonParser.parseString(classModuleLocation).getAsJsonObject();
        assert classModuleJson.has("id");
        final long classModuleId = classModuleJson.get("id").getAsLong();

        MockMultipartFile file = getTestFile();
        String location = mockMvc.perform(multipart(String.format("/exam/module/%s", classModuleId))
                        .file(file)
                        .with(OAuth2TestUtil.mockLecturerOidcLogin())
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/exam/")))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        assert location != null;
        mockMvc.perform(get(location)
                        .with(OAuth2TestUtil.mockLecturerOidcLogin())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.class_id").value(classId.toString()))
                .andExpect(jsonPath("$.module_id").value(moduleId))
                .andExpect(jsonPath("$.max_score").isNumber())
                .andExpect(jsonPath("$.max_score", is(41)))
                .andExpect(jsonPath("$.date").isString())
                .andExpect(jsonPath("$.date", is("2023-12-22 00:00:00")))
                .andExpect(jsonPath("$.exam_results").isArray());
        // Define claims for the ID token
        Map<String, Object> claims = Map.of(
                "sub", map.get("abc@tn.mischok.academy"),
                "name", "John Doe",
                "email", "abc@tn.mischok.academy"
        );

        // Create a mock ID token with claims
        OidcIdToken idToken = new OidcIdToken(
                map.get("abc@tn.mischok.academy").toString(),
                Instant.now(),
                Instant.now().plusSeconds(3600),
                claims
        );

        // Create a mock principal with authorities and ID token
        OidcUser principal = new DefaultOidcUser(
                List.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_U20230901UFI")),
                idToken
        );

        // Create an OAuth2AuthenticationToken with the mock principal
        OAuth2AuthenticationToken authenticationToken = new OAuth2AuthenticationToken(
                principal,
                principal.getAuthorities(),
                "random-id"
        );
        RequestPostProcessor requestPostProcessor = SecurityMockMvcRequestPostProcessors.authentication(authenticationToken);

        mockMvc.perform(get(location)
                        .with(requestPostProcessor)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.class_id").value(classId.toString()))
                .andExpect(jsonPath("$.module_id").value(moduleId))
                .andExpect(jsonPath("$.max_score").isNumber())
                .andExpect(jsonPath("$.max_score", is(41)))
                .andExpect(jsonPath("$.date").isString())
                .andExpect(jsonPath("$.date", is("2023-12-22 00:00:00")))
                .andExpect(jsonPath("$.exam_results").isArray())
                .andExpect(jsonPath("$.exam_results.length()").value(1));

    }

private MockMultipartFile getTestFile() throws IOException {
    ClassPathResource resource = new ClassPathResource("test.xlsx");
    byte[] content;
    try {
        content = Files.readAllBytes(resource.getFile().toPath());
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    return new MockMultipartFile(
            "file",
            resource.getFilename(),
            MediaType.TEXT_PLAIN_VALUE,
            content
    );
}

private Map<String, UUID> mockMethods() {
    final Map<String, UUID> results = new HashMap<>();
    JsonArray array = new JsonArray();
    for (String email : List.of("abc@tn.mischok.academy", "abc2@tn.mischok.academy", "abc3@tn.mischok.academy")) {
        results.put(email, UUID.randomUUID());
        JsonObject dummyJson = new JsonObject();
        dummyJson.addProperty("id", results.get(email).toString());
        dummyJson.addProperty("mail", email);
        array.add(dummyJson);
    }
    JsonObject dummyJson = new JsonObject();
    dummyJson.add("value", array);
    stubFor(WireMock.get(urlEqualTo(String.format("/v1.0/groups/%s/members?$select=id,mail", "e0f0c03a-340a-4a77-91b1-ff1807345d6e")))
            .willReturn(aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(dummyJson.toString())));
    return results;
}
}