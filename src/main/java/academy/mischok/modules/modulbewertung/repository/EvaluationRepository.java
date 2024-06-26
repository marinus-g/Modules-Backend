package academy.mischok.modules.modulbewertung.repository;


import academy.mischok.modules.modulbewertung.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
}
