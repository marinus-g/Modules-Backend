package academy.mischok.modules.controller;

import academy.mischok.modules.dto.ExamDto;
import academy.mischok.modules.dto.ExamResultDto;
import academy.mischok.modules.exception.InvalidExcelFormatExamException;
import academy.mischok.modules.exception.ModuleNotFoundException;
import academy.mischok.modules.model.OAuthUser;
import academy.mischok.modules.service.ExamService;
import academy.mischok.modules.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Optional;

@RestController
@RequestMapping("/exam")
@RequiredArgsConstructor
@Slf4j
public class ExamController {

    private final ExamService examService;
    private final SimpleDateFormat simpleDateFormat;
    private final UserService userService;


    @PostMapping(path = "/module/{moduleId}")
    @PreAuthorize("hasRole('ROLE_Dozentenkollegium')")
    public ResponseEntity<Void> uploadExam(@RequestParam("file") MultipartFile file,
                                           @PathVariable("moduleId") Long moduleId) throws ModuleNotFoundException,
            InvalidExcelFormatExamException {
        log.info("Uploading exam for module {}", moduleId);
        return ResponseEntity.created(URI.create(String.format("/exam/%s", examService.uploadExam(file, moduleId).getId()))).build();
    }

    @GetMapping(path = "/{examId}")
    public ResponseEntity<ExamDto> getExam(@PathVariable("examId") Long examId) {
        log.info("Getting exam with id {}", examId);
        return examService.findExamById(examId)
                .map(exam -> ExamDto
                        .builder()
                        .id(exam.getId())
                        .moduleId(exam.getClassModule().getModule().getId())
                        .classId(exam.getClassModule().getSchoolClass())
                        .maxScore(exam.getMaxScore())
                        .date(simpleDateFormat.format(exam.getDate()))
                        .examResults(exam
                                .getExamResults()
                                .stream()
                                .map(examResult -> ExamResultDto
                                        .builder()
                                        .userId(examResult.getUserId())
                                        .score(examResult.getScore())
                                        .grade(examService.calculateGrade(examResult.getScore(), exam.getMaxScore()))
                                        .lastName(userService.findUserById(examResult.getUserId()).map(OAuthUser::getLastName).orElse(null))
                                        .firstName(userService.findUserById(examResult.getUserId()).map(OAuthUser::getFirstName).orElse(null))
                                        .state(examResult.getState())
                                        .build())
                                .toList())
                        .build())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
