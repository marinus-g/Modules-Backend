package academy.mischok.modules.service;

import academy.mischok.modules.exception.InvalidExcelFormatExamException;
import academy.mischok.modules.exception.ModuleNotFoundException;
import academy.mischok.modules.model.Exam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ExamService {


    Exam uploadExam(MultipartFile file, Long moduleId) throws InvalidExcelFormatExamException, ModuleNotFoundException;

    Optional<Exam> findExamById(Long examId);

    Integer calculateGrade(Integer score, Integer maxScore);
}
