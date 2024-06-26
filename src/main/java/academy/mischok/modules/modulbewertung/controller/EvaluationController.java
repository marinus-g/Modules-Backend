package academy.mischok.modules.modulbewertung.controller;

import academy.mischok.modules.modulbewertung.model.Evaluation;
import academy.mischok.modules.modulbewertung.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/evaluations")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    @GetMapping
    public String getAllEvaluations(Model model) {
        List<Evaluation> evaluations = evaluationService.getAllEvaluations();
        model.addAttribute("evaluations", evaluations);
        return "evaluation/list";
    }

    @GetMapping("/{id}")
    public String getEvaluation(@PathVariable Long id, Model model) {
        Evaluation evaluation = evaluationService.getEvaluation(id).orElse(null);
        model.addAttribute("evaluation", evaluation);
        return "evaluation/detail";
    }

    @GetMapping("/new")
    public String newEvaluationForm(Model model) {
        model.addAttribute("evaluation", new Evaluation());
        return "evaluation/form";
    }

    @PostMapping
    public String saveEvaluation(@ModelAttribute Evaluation evaluation) {
        evaluationService.saveEvaluation(evaluation);
        return "redirect:/evaluations";
    }

    @GetMapping("/delete/{id}")
    public String deleteEvaluation(@PathVariable Long id) {
        evaluationService.deleteEvaluation(id);
        return "redirect:/evaluations";
    }
}
