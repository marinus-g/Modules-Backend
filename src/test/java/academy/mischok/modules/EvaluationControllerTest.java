package academy.mischok.modules;

import academy.mischok.modules.modulbewertung.controller.EvaluationController;
import academy.mischok.modules.modulbewertung.model.Evaluation;
import academy.mischok.modules.modulbewertung.service.EvaluationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        this.mockMvc.perform(get("/evaluations"))
                .andExpect(status().isOk())
                .andExpect(view().name("evaluation/list"))
                .andExpect(model().attributeExists("evaluations"))
                .andExpect(model().attribute("evaluations", Arrays.asList(evaluation1, evaluation2)));
    }

    @Test
    public void testGetEvaluation() throws Exception {
        Evaluation evaluation = new Evaluation(1L, 80, 90, null);

        given(evaluationService.getEvaluation(anyLong())).willReturn(Optional.of(evaluation));

        this.mockMvc.perform(get("/evaluations/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("evaluation/detail"))
                .andExpect(model().attributeExists("evaluation"))
                .andExpect(model().attribute("evaluation", evaluation));
    }

    @Test
    public void testNewEvaluationForm() throws Exception {
        this.mockMvc.perform(get("/evaluations/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("evaluation/form"))
                .andExpect(model().attributeExists("evaluation"));
    }

    @Test
    public void testDeleteEvaluation() throws Exception {
        this.mockMvc.perform(get("/evaluations/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/evaluations"));
    }
}