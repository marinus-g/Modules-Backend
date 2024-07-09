package academy.mischok.modules.controller;

import academy.mischok.modules.configuration.OAuth2ClientConfiguration;
import academy.mischok.modules.util.OAuth2TestUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(OAuth2ClientConfiguration.class)
class ClassControllerTest extends AbstractControllerTest {

    @Test
    @WithMockUser(roles = "U20230901UFI")
    void testGetMeClass() throws Exception {
        mockAuthClient();
        mockMvc.perform(get("/class/me")
                        .with(OAuth2TestUtil.mockOidcLogin())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("U20230901UFI"));
    }

    @Test
    void testGetClasses() throws Exception {
        mockAuthClient();
        mockMvc.perform(get("/class")
                        .with(OAuth2TestUtil.mockLecturerOidcLogin())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testAddModuleToClass() throws Exception {
        mockAuthClient();
        final String moduleData = """
                {
                "name": "classModuleName",
                "description": "uniqueModuleDescription"
                }""";

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

        assert moduleLocation != null;

        final Long moduleId = Long.parseLong(moduleLocation.substring(moduleLocation.lastIndexOf("/") + 1));

        final String meClassObject = mockMvc.perform(get("/class/me")
                        .with(OAuth2TestUtil.mockOidcLogin())
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonObject meClassJson = JsonParser.parseString(meClassObject).getAsJsonObject();
        assert meClassJson.has("id");
        final long classId = meClassJson.get("id").getAsLong();
        mockMvc.perform(post("/class/" + classId + "/module/" + moduleId)
                        .with(OAuth2TestUtil.mockLecturerOidcLogin())
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/class/" + classId + "/module/" + moduleId)));

        mockAuthClient();
        mockMvc.perform(get("/class/" + classId + "/module/" + moduleId)
                        .with(OAuth2TestUtil.mockOidcLogin())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("classModuleName"))
                .andExpect(jsonPath("$.data.description").value("uniqueModuleDescription"))
                .andExpect(jsonPath("$.data.id").value(moduleId));

        mockMvc.perform(get("/class/" + classId + "/modules")
                        .with(OAuth2TestUtil.mockOidcLogin())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", greaterThan(0)));
    }
}
