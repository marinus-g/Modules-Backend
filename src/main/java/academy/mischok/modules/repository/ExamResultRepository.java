package academy.mischok.modules.repository;

import academy.mischok.modules.model.ExamResult;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {

    Optional<ExamResult> findByIdAndExam_Id(Long id, Long id1);


}