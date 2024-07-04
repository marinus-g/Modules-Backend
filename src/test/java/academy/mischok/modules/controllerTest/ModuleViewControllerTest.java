package academy.mischok.modules.controllerTest;

import academy.mischok.modules.dtos.ModuleOverviewDto;
import academy.mischok.modules.service.ModuleViewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    public void testGetModuleOverviewByModuleId() throws Exception {
        Long moduleId = 1L;
        List<ModuleOverviewDto> mockModuleOverview = Arrays.asList(
                new ModuleOverviewDto("Module 1", "Description 1", new Date(), "A", "B"),
                new ModuleOverviewDto("Module 2", "Description 2", new Date(), "C", "D")
        );
        when(moduleViewService.getModuleOverviewByModuleId(moduleId)).thenReturn(mockModuleOverview);

        mockMvc.perform(get("/modules/{moduleId}", moduleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(is("Module 1")))
                .andExpect(jsonPath("$[1].name").value(is("Module 2")));
    }
}
