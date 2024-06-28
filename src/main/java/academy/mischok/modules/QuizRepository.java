package academy.mischok.modules;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository  extends JpaRepository<QuizExcelEntity, Long> {
}
