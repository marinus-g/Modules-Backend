package academy.mischok.modules.controllerTest;

import academy.mischok.modules.dtos.ModuleDto;
import academy.mischok.modules.dtos.ModuleOverviewDto;
import academy.mischok.modules.model.ModuleEntity;
import academy.mischok.modules.repository.ModuleRepository;
import academy.mischok.modules.service.ModuleViewService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.lang.Character.getName;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"})
@AutoConfigureMockMvc
@Transactional
public class ModuleViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModuleViewService moduleViewService;
    @Autowired
    private ModuleRepository moduleRepository;

    @Test
    @WithMockUser
    public void testGetModuleOverviewByModuleId() throws Exception {
        Long moduleId = 1L;
        List<ModuleOverviewDto> mockModuleOverview = Arrays.asList(
                new ModuleOverviewDto("Module 1", "Description 1", new Date(), "A", "B"),
                new ModuleOverviewDto("Module 2", "Description 2", new Date(), "C", "D")
        );
        when(moduleViewService.getModuleOverviewByModuleId(moduleId)).thenReturn(mockModuleOverview);

        mockMvc.perform(get("/modules/{moduleId}", moduleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].moduleName").value(is("Module 1")))
                .andExpect(jsonPath("$[1].moduleName").value(is("Module 2")));
    }

    @Test
    public void testGetModuleByName() {
        String moduleName = "TestModule";
        ModuleEntity mockModuleEntity = new ModuleEntity();
        mockModuleEntity.setName(moduleName);
        mockModuleEntity.setDescription("TestDescription");

        ModuleDto expectedDto = new ModuleDto();
        expectedDto.setName(moduleName);
        expectedDto.setDescription("TestDescription");

        when(moduleRepository.findByName(moduleName)).thenReturn(Optional.of(mockModuleEntity));

        Optional<ModuleDto> result = moduleViewService.getModuleByName(moduleName);

        assertTrue(result.isPresent());
        assertEquals(expectedDto.getName(), result.get().getName());
        assertEquals(expectedDto.getDescription(), result.get().getDescription());
    }

}
