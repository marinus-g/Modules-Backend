package academy.mischok.modules;

import academy.mischok.modules.model.Module;
import academy.mischok.modules.service.ModuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
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
    public void testGetAllModules() throws Exception {
        Module module1 = new Module(1L, "Module 1", null);
        Module module2 = new Module(2L, "Module 2", null);

        given(moduleService.getAllModules()).willReturn(Arrays.asList(module1, module2));

        this.mockMvc.perform(get("/api/modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Module 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Module 2"));
    }

    @Test
    public void testGetModule() throws Exception {
        Module module = new Module(1L, "Module 1", null);

        given(moduleService.getModule(anyLong())).willReturn(Optional.of(module));

        this.mockMvc.perform(get("/api/modules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Module 1"));
    }

    @Test
    public void testSaveModule() throws Exception {
        Module module = new Module(1L, "Module 1", null);

        given(moduleService.saveModule(any(Module.class))).willReturn(module);

        String requestBody = "{\"name\":\"Module 1\"}";

        this.mockMvc.perform(post("/api/modules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))  // Überprüft, dass 'id' vorhanden ist
                .andExpect(jsonPath("$.name").value("Module 1"));
    }

    @Test
    public void testDeleteModule() throws Exception {
        this.mockMvc.perform(delete("/api/modules/1"))
                .andExpect(status().isOk());
    }
}
