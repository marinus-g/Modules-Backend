package academy.mischok.modules.service.impl;

import academy.mischok.modules.exception.InvalidExcelFormatExamException;
import academy.mischok.modules.exception.ModuleNotFoundException;
import academy.mischok.modules.model.*;
import academy.mischok.modules.repository.ClassModuleRepository;
import academy.mischok.modules.repository.ExamRepository;
import academy.mischok.modules.repository.ExamResultRepository;
import academy.mischok.modules.service.ClassService;
import academy.mischok.modules.service.ExamService;
import academy.mischok.modules.service.ModuleService;
import academy.mischok.modules.service.UserService;
import academy.mischok.modules.util.RegisteredExamSheetRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class ExamServiceImpl implements ExamService {

    private final ClassService classService;
    private final UserService userService;
    private final ModuleService moduleService;
    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;

    @Override
    public Exam uploadExam(MultipartFile file, Long moduleId) throws InvalidExcelFormatExamException, ModuleNotFoundException {
        final ClassModule classModule = moduleService.findClassModuleById(moduleId).orElseThrow(() ->
                new ModuleNotFoundException("Classmodule with id " + moduleId + " not found"));
        if (Objects.nonNull(classModule.getExam())) {
            throw new InvalidExcelFormatExamException("Exam already uploaded");
        }
        Workbook workbook = multipartFileToExcelWorkbook(file);
        if (workbook == null) {
            throw new InvalidExcelFormatExamException("Invalid Excel format");
        }
        Exam exam = examRepository.findByClassModule_Id(moduleId)
                .map(exam1 -> {
                    exam1.getExamResults().clear();
                    return examRepository.saveAndFlush(exam1);
                }).orElseGet(() -> examRepository.save(Exam
                        .builder()
                        .classModule(classModule)
                        .examResults(new ArrayList<>())
                        .build()));
        Sheet sheet = getSheet(workbook);
        int max = sheet.getLastRowNum();
        for (int i = 2; i < max; i++) {
            ExamResult result = processRow(sheet, i, exam);
            if (result == null) {
                continue;
            }
            if (Objects.isNull(result.getUserId()) || Objects.isNull(result.getState())) {
                continue;
            }
            result = examResultRepository.save(result);
            exam.getExamResults().add(result);
        }
        exam = examRepository.save(exam);
        classModule.setExam(exam);
        classService.save(classModule);
        log.info("Exam with id {} uploaded", exam.getId());
        return exam;
    }

    @Override
    public Optional<Exam> findExamById(Long examId) {
        final OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        boolean lecturer = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_Dozentenkollegium"));
        if (lecturer) {
            return examRepository.findById(examId);
        } else {
            final OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            final UUID userId = UUID.fromString(Objects.requireNonNull(oidcUser.getAttribute("oid")).toString());
            return examRepository.findById(examId)
                    .map(exam -> {
                        exam.setExamResults(List.of(this.examResultRepository.findByUserIdAndExam_Id(userId,
                                examId)
                                .orElseThrow(() -> new NoSuchElementException("Exam result not found"))));
                        log.info("Exam: {}", exam.toString());
                        return exam;
                    });
        }
    }

    @Override
    public Integer calculateGrade(Integer score, Integer maxScore) {
        if (maxScore == null || maxScore == 0 || score == null) {
            return null;
        }
        final double percentage = this.calculatePercentage(score, maxScore);
        if (percentage >= 92) {
            return 1;
        } else if (percentage >= 81) {
            return 2;
        } else if (percentage >= 67) {
            return 3;
        } else if (percentage >= 50) {
            return 4;
        } else if (percentage >= 30){
            return 5;
        } else {
            return 0;
        }
    }

    private double calculatePercentage(Integer score, Integer maxScore) {
        return (double) score / maxScore * 100;
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
                    final Optional<UUID> userId = userService.findIdByEmail(exam.getClassModule().getSchoolClass(), cell.getStringCellValue());
                    if (userId.isEmpty()) {
                        log.info("User with email {} not found", cell.getStringCellValue());
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
