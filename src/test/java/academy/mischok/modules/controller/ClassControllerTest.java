package academy.mischok.modules.controller;

import academy.mischok.modules.configuration.OAuth2ClientConfiguration;
import academy.mischok.modules.util.OAuth2TestUtil;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
