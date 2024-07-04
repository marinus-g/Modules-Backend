package academy.mischok.modules.excelimport.service;

import academy.mischok.modules.excelimport.ExcelHelper;
import academy.mischok.modules.excelimport.repository.QuizRepository;
import academy.mischok.modules.excelimport.model.QuizExcelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ExcelService {
    @Autowired
    QuizRepository quizRepository;

    public void save(MultipartFile file) {
        try {
            List<QuizExcelEntity> quizzes = ExcelHelper.excelToQuiz(file.getInputStream());
            quizRepository.saveAll(quizzes);
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

    public List<QuizExcelEntity> getAllQuizzes() {
        return quizRepository.findAll();
    }
}
