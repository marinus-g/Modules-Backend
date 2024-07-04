package academy.mischok.modules.excelimport.repository;

import academy.mischok.modules.excelimport.model.QuizExcelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository  extends JpaRepository<QuizExcelEntity, Long> {
}
