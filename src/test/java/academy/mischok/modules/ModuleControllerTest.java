package academy.mischok.modules;

import academy.mischok.modules.modulbewertung.controller.ModuleController;
import academy.mischok.modules.modulbewertung.model.Module;
import academy.mischok.modules.modulbewertung.service.ModuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ModuleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ModuleService moduleService;

    @InjectMocks
    private ModuleController moduleController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(moduleController).build();
    }

    @Test
    public void testEditModuleForm() throws Exception {
        Module module = new Module(1L, "Module 1", null);

        given(moduleService.getModule(anyLong())).willReturn(Optional.of(module));

        this.mockMvc.perform(get("/modules/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("module/edit_form"))
                .andExpect(model().attributeExists("module"))
                .andExpect(model().attribute("module", module));
    }

    @Test
    public void testEditModule() throws Exception {
        Module module = new Module(1L, "Module 1", null);

        given(moduleService.saveModule(any(Module.class))).willReturn(module);

        this.mockMvc.perform(post("/modules/edit/1")
                        .param("id", "1")
                        .param("name", "Updated Module Name"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/modules/1"));
    }

    @Test
    public void testGetAllModules() throws Exception {
        Module module1 = new Module(1L, "Module 1", null);
        Module module2 = new Module(2L, "Module 2", null);

        given(moduleService.getAllModules()).willReturn(Arrays.asList(module1, module2));

        this.mockMvc.perform(get("/modules"))
                .andExpect(status().isOk())
                .andExpect(view().name("module/list"))
                .andExpect(model().attributeExists("modules"))
                .andExpect(model().attribute("modules", Arrays.asList(module1, module2)));
    }

    @Test
    public void testGetModule() throws Exception {
        Module module = new Module(1L, "Module 1", null);

        given(moduleService.getModule(anyLong())).willReturn(Optional.of(module));

        this.mockMvc.perform(get("/modules/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("module/detail"))
                .andExpect(model().attributeExists("module"))
                .andExpect(model().attribute("module", module));
    }

    @Test
    public void testNewModuleForm() throws Exception {
        this.mockMvc.perform(get("/modules/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("module/form"))
                .andExpect(model().attributeExists("module"));
    }

    @Test
    public void testDeleteModule() throws Exception {
        this.mockMvc.perform(get("/modules/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/modules"));
    }
}
