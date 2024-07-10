package academy.mischok.modules.service.impl;

import academy.mischok.modules.exception.InvalidExcelFormatExamException;
import academy.mischok.modules.exception.ModuleNotFoundException;
import academy.mischok.modules.model.*;
import academy.mischok.modules.repository.ExamRepository;
import academy.mischok.modules.repository.ExamResultRepository;
import academy.mischok.modules.service.ExamService;
import academy.mischok.modules.service.ModuleService;
import academy.mischok.modules.service.UserService;
import academy.mischok.modules.util.RegisteredExamSheetRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamServiceImpl implements ExamService {

    private final UserService userService;
    private final ModuleService moduleService;
    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;

    @Override
    public Exam uploadExam(MultipartFile file, Long moduleId) throws InvalidExcelFormatExamException, ModuleNotFoundException {
        Workbook workbook = multipartFileToExcelWorkbook(file);
        if (workbook == null) {
            throw new InvalidExcelFormatExamException("Invalid Excel format");
        }
        final ClassModule classModule = moduleService.findClassModuleById(moduleId).orElseThrow(() ->
                new ModuleNotFoundException("Classmodule with id " + moduleId + " not found"));
        Exam exam = examRepository.save(Exam
                .builder()
                .classModule(classModule)
                .examResults(new ArrayList<>())
                .build());
        Sheet sheet = getSheet(workbook);
        int max = sheet.getLastRowNum();
        for (int i = 2; i < max; i++) {
            ExamResult result = processRow(sheet, i, exam);
            if (result == null) {
                continue;
            }
            if (Objects.isNull(result.getScore()) || Objects.isNull(result.getUserId()) || Objects.isNull(result.getState())) {
                continue;
            }
            result = examResultRepository.save(result);
            exam.getExamResults().add(result);
        }
        examRepository.save(exam);
        log.info("Exam with id {} uploaded", exam.getId());
        return exam;
    }

    @Override
    public Optional<Exam> findExamById(Long examId) {
        return this.examRepository.findById(examId);
    }

    @Override
    public Integer calculateGrade(Integer score, Integer maxScore) {
        return 1;
    }

    private ExamResult processRow(Sheet sheet, int i, Exam exam) {
        Row row = sheet.getRow(i);
        if (row == null) {
            return null;
        }
        ExamResult examResult = ExamResult.builder()
                .exam(exam)
                .build();
        for (RegisteredExamSheetRow registeredRow : RegisteredExamSheetRow.values()) {
            Cell cell = row.getCell(registeredRow.getIndex());
            if (!isValidCell(cell)) {
                continue;
            }

            switch (registeredRow) {
                case EMAIL -> {
                    log.info("User with email {} found", cell.getStringCellValue());
                    final Optional<UUID> userId = userService.findIdByEmail(cell.getStringCellValue());
                    if (userId.isEmpty()) {
                        continue;
                    }
                    examResult.setUserId(userId.get());
                }
                case DATE ->
                        exam.setDate(new Date(((java.util.Date) Objects.requireNonNull(processCell(cell, registeredRow))).getTime()));
                case STATUS ->
                        examResult.setState(ExamState.getFromGermanString(Objects.requireNonNull(processCell(cell, registeredRow))));
                case POINTS -> examResult.setScore(processCell(cell, registeredRow));
                case MAX_POINTS -> {
                    if (exam.getMaxScore() == null) {
                        exam.setMaxScore(processCell(cell, registeredRow));
                    }
                }
            }
        }
        return examResult;
    }

    private <T> T processCell(Cell cell, RegisteredExamSheetRow registeredRow) {
        return switch (registeredRow) {
            case STATUS -> (T) cell.getStringCellValue();
            case DATE -> (T) cell.getDateCellValue();
            case POINTS, MAX_POINTS -> (T) (Integer) (int) cell.getNumericCellValue();
            default -> null;
        };
    }

    private boolean isValidCell(Cell cell) {
        return cell != null
                && cell.getCellType() != CellType.BLANK
                && (cell.getCellType() != CellType.STRING
                || (
                !cell.getStringCellValue().isEmpty()
                        && !cell.getStringCellValue().equals("null"))
        );
    }

    private Sheet getSheet(Workbook workbook) {
        return workbook.getSheetAt(0);
    }

    private Workbook multipartFileToExcelWorkbook(MultipartFile file) throws InvalidExcelFormatExamException {
        try {
            return new XSSFWorkbook(file.getInputStream());
        } catch (IOException e) {
            throw new InvalidExcelFormatExamException("Invalid Excel format");
        }
    }
}
