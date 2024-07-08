package academy.mischok.modules.controller;

import academy.mischok.modules.configuration.OAuth2ClientConfiguration;
import academy.mischok.modules.util.OAuth2TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(OAuth2ClientConfiguration.class)
class ModuleControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    ModuleControllerTest(WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }


    @Test
    @WithMockUser(roles = "Dozentenkollegium")
    void testCreateModule() throws Exception {

        final String data = """
                {
                "name": "uniqueModuleName",
                "description": "uniqueModuleDescription"
                }""";

        String location = mockMvc.perform(post("/module")
                        .content(data)
                        .with(OAuth2TestUtil.mockOidcLogin())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/module/")))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        assertThat(location).isNotNull();

        mockMvc.perform(get(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(OAuth2TestUtil.mockOidcLogin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("uniqueModuleName"))
                .andExpect(jsonPath("$.description").value("uniqueModuleDescription"))
                .andExpect(jsonPath("$.id").isNumber());

    }
}
