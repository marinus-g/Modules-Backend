package academy.mischok.modules.service;

import academy.mischok.modules.model.Evaluation;
import academy.mischok.modules.repository.EvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EvaluationService {

    @Autowired
    private EvaluationRepository evaluationRepository;

    public List<Evaluation> getAllEvaluations() {
        return evaluationRepository.findAll();
    }

    public Optional<Evaluation> getEvaluation(Long id) {
        return evaluationRepository.findById(id);
    }

    public Evaluation saveEvaluation(Evaluation evaluation) {
        return evaluationRepository.save(evaluation);
    }

    public void deleteEvaluation(Long id) {
        evaluationRepository.deleteById(id);
    }

    public String calculateGrade(Evaluation evaluation) {
        double totalScore = evaluation.getTotalScore();
        return evaluation.getGrade();
    }
}
