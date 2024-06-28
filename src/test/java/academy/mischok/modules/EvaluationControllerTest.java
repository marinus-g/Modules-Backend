package academy.mischok.modules;

import academy.mischok.modules.modulbewertung.controller.EvaluationController;
import academy.mischok.modules.modulbewertung.model.Evaluation;
import academy.mischok.modules.modulbewertung.service.EvaluationService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EvaluationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EvaluationService evaluationService;

    @InjectMocks
    private EvaluationController evaluationController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(evaluationController).build();
    }

    @Test
    public void testGetAllEvaluations() throws Exception {
        Evaluation evaluation1 = new Evaluation(1L, 80, 90, null);
        Evaluation evaluation2 = new Evaluation(2L, 70, 60, null);

        given(evaluationService.getAllEvaluations()).willReturn(Arrays.asList(evaluation1, evaluation2));

        this.mockMvc.perform(get("/api/evaluations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].projectScore").value(80))
                .andExpect(jsonPath("$[0].examScore").value(90))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].projectScore").value(70))
                .andExpect(jsonPath("$[1].examScore").value(60));
    }

    @Test
    public void testGetEvaluation() throws Exception {
        Evaluation evaluation = new Evaluation(1L, 80, 90, null);

        given(evaluationService.getEvaluation(anyLong())).willReturn(Optional.of(evaluation));

        this.mockMvc.perform(get("/api/evaluations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())  // Überprüft, dass 'id' vorhanden ist
                .andExpect(jsonPath("$.projectScore").value(80))
                .andExpect(jsonPath("$.examScore").value(90));
    }


    @Test
    public void testSaveEvaluation() throws Exception {
        Evaluation evaluation = new Evaluation(1L, 80, 90, null);

        // Mocking des saveEvaluation-Aufrufs
        given(evaluationService.saveEvaluation(any(Evaluation.class))).willReturn(evaluation);

        String requestBody = "{\"projectScore\":80,\"examScore\":90}";

        this.mockMvc.perform(post("/api/evaluations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())  // Überprüft, dass 'id' vorhanden ist
                .andExpect(jsonPath("$.projectScore").value(80))
                .andExpect(jsonPath("$.examScore").value(90));
    }

    @Test
    public void testDeleteEvaluation() throws Exception {
        this.mockMvc.perform(delete("/api/evaluations/1"))
                .andExpect(status().isOk());
    }
}
