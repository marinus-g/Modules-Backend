package academy.mischok.modules.repository;

import academy.mischok.modules.model.ExamResult;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamMemberRepository extends JpaRepository<ExamResult, Long> {


}
