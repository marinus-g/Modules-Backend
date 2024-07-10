package academy.mischok.modules.repository;

import academy.mischok.modules.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<Exam, Long> {

}