package academy.mischok.modules.controller;

import academy.mischok.modules.util.OAuth2TestUtil;
import academy.mischok.modules.util.Tuple;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(httpsEnabled = true, httpsPort = 0, proxyMode = true)
class ProjectControllerTest extends AbstractControllerTest {


    @Test
    void testCreateProject() throws Exception {
        final String data = """
                {
                    "name": "Test Project",
                    "description": "Test Description",
                    "class_id": "%s",
                    "module_id": %s
                }
                """;

        Tuple<UUID, Long> classAndModule = getMeClassAndClassAndModule();
        final UUID classId = classAndModule.first();
        final Long moduleId = classAndModule.second();

        mockAuthClient();
        String location = mockMvc.perform(post("/project")
                        .content(String.format(data, classId, moduleId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(OAuth2TestUtil.mockLecturerOidcLogin()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/project/")))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        assertThat(location).isNotNull();

        mockMvc.perform(get(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(OAuth2TestUtil.mockOidcLogin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.class_id").value(classId.toString()))
                .andExpect(jsonPath("$.module_id").value(moduleId));

        final Long projectId = Long.parseLong(location.split("/")[location.split("/").length - 1]);

        mockMvc.perform(get("/class/" + classId + "/module/" + moduleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(OAuth2TestUtil.mockOidcLogin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.project_id").isNumber())
                .andExpect(jsonPath("$.project_id").value(projectId));
    }

    private Tuple<UUID, Long> getMeClassAndClassAndModule() throws Exception {
        mockAuthClient();
        final String moduleName = UUID.randomUUID().toString().split("-")[0];
        final String data = """
                {
                "name": "%s",
                "description": "uniqueModuleDescription"
                }""";

        String moduleLocation = mockMvc.perform(post("/module")
                        .with(OAuth2TestUtil.mockLecturerOidcLogin())
                        .content(String.format(data, moduleName))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/module/")))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        assertThat(moduleLocation).isNotNull();

        mockMvc.perform(get(moduleLocation)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(OAuth2TestUtil.mockOidcLogin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(moduleName))
                .andExpect(jsonPath("$.description").value("uniqueModuleDescription"))
                .andExpect(jsonPath("$.id").isNumber());

        final Long moduleId = Long.parseLong(moduleLocation.split("/")[moduleLocation.split("/").length - 1]);
        final String meClassObject = mockMvc.perform(get("/class/me")
                        .with(OAuth2TestUtil.mockOidcLogin())
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonObject meClassJson = JsonParser.parseString(meClassObject).getAsJsonObject();
        assert meClassJson.has("id");
        final UUID classId = UUID.fromString(meClassJson.get("id").getAsString());
        mockMvc.perform(post("/class/" + classId + "/module/" + moduleId)
                        .with(OAuth2TestUtil.mockLecturerOidcLogin())
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/class/" + classId + "/module/" + moduleId)));
        return new Tuple<>(classId, moduleId);
    }
}
