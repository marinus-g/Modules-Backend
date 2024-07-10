package academy.mischok.modules.controller;

import academy.mischok.modules.service.UserService;
import academy.mischok.modules.util.OAuth2TestUtil;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
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
import org.springframework.security.web.FilterChainProxy;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(httpsEnabled = true, httpsPort = 8080, proxyMode = true)
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
        final long classId = meClassJson.get("id").getAsLong();
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
                .andExpect(jsonPath("$.class_id").value(classId))
                .andExpect(jsonPath("$.module_id").value(moduleId))
                .andExpect(jsonPath("$.max_score").isNumber())
                .andExpect(jsonPath("$.max_score", is(41)))
                .andExpect(jsonPath("$.date").isString())
                .andExpect(jsonPath("$.date", is("2023-12-22 00:00:00")))
                .andExpect(jsonPath("$.exam_results").isArray());

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
        UserService userService = Mockito.mock(UserService.class);
        for (String email : List.of("abc@tn.mischok.academy", "abc2@tn.mischok.academy", "abc3@tn.mischok.academy")) {
            when(userService.existsByEmail(email)).thenReturn(true);
            when(userService.findIdByEmail(email)).thenReturn(Optional.of(results.computeIfAbsent(email, k -> UUID.randomUUID()))); // this is some xd shit for tests
            JsonObject dummyJson = new JsonObject();
            dummyJson.addProperty("id", results.get(email).toString());
            stubFor(WireMock.get(urlEqualTo(String.format("/v1.0/users/%s?$select=id", email)))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBody(dummyJson.toString())));
        }
        when(userService.existsByEmail(""))
                .thenReturn(false);
        when(userService.findIdByEmail(""))
                .thenReturn(Optional.empty());
        return results;
    }
}