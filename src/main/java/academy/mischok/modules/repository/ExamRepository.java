package academy.mischok.modules.repository;

import academy.mischok.modules.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    Optional<Exam> findByClassModule_Id(Long id);


}