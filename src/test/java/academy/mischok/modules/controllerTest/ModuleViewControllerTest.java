package academy.mischok.modules.controllerTest;

import academy.mischok.modules.service.ModuleViewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ModuleViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModuleViewService moduleViewService;

    @Test
    public void testGetModule() throws Exception {
        when(moduleViewService.getAllModules()).thenReturn();

        mockMvc.perform(get("/modules"))
                .andExpect(status().isOk());
    }

}
