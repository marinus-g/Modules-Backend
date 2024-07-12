package academy.mischok.modules.controller;

import academy.mischok.modules.dto.ClassDto;
import academy.mischok.modules.dto.ClassModuleDto;
import academy.mischok.modules.dto.ModuleDto;
import academy.mischok.modules.exception.AuthorizationException;
import academy.mischok.modules.exception.ModuleAlreadyPresentException;
import academy.mischok.modules.exception.ModuleNotFoundException;
import academy.mischok.modules.exception.SchoolClassNotFoundException;
import academy.mischok.modules.model.Exam;
import academy.mischok.modules.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/class")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;
    private final SimpleDateFormat simpleDateFormat;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_Dozentenkollegium')")
    public ResponseEntity<List<ClassDto>> getClasses() {
        return ResponseEntity.ok(this.classService
                .findClasses()
                .stream().map(
                        schoolClass -> ClassDto.builder()
                                .id(schoolClass.getClassId())
                                .name(schoolClass.getName())
                                .build())
                .toList());
    }

    @GetMapping("/me")
    public ResponseEntity<ClassDto> getClass(OAuth2AuthenticationToken token) {
        return this.classService.findClassByUser(token)
                .map(schoolClass -> ClassDto.builder()
                        .id(schoolClass.getClassId())
                        .name(schoolClass.getName())
                        .build())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(path = "/{classId}/module/{moduleId}")
    @PreAuthorize("hasRole('ROLE_Dozentenkollegium')")
    public ResponseEntity<Void> addModuleToClass(@PathVariable UUID classId, @PathVariable Long moduleId) throws ModuleNotFoundException,
            SchoolClassNotFoundException, ModuleAlreadyPresentException {
        this.classService.addModuleToClass(classId, moduleId);
        return ResponseEntity.created(URI.create(String.format("/class/%s/module/%s", classId, moduleId))).build();
    }

    @DeleteMapping(path = "/{classId}/module/{moduleId}")
    @PreAuthorize("hasRole('ROLE_Dozentenkollegium')")
    public ResponseEntity<Void> removeModuleFromClass(@PathVariable UUID classId, @PathVariable Long moduleId) throws ModuleNotFoundException,
            SchoolClassNotFoundException {
        this.classService.removeModuleFromClass(classId, moduleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{classId}/module/{moduleId}")
    public ResponseEntity<ClassModuleDto> getClassModule(OAuth2AuthenticationToken token,
                                                         @PathVariable UUID classId,
                                                         @PathVariable Long moduleId)
            throws SchoolClassNotFoundException, AuthorizationException {
        return this.classService.findClassModule(token, classId, moduleId)
                .map(classModule -> ClassModuleDto.builder()
                        .id(classModule.getId())
                        .startDate(simpleDateFormat.format(classModule.getStartDate()))
                        .examId(Optional.ofNullable(classModule.getExam()).map(Exam::getId).orElse(null))
                        .data(ModuleDto.builder()
                                .id(classModule.getModule().getId())
                                .name(classModule.getModule().getName())
                                .description(classModule.getModule().getDescription())
                                .build())
                        .build())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{classId}")
    public ResponseEntity<ClassDto> getClass(OAuth2AuthenticationToken token, @PathVariable UUID classId) throws AuthorizationException {
        return this.classService.findClassById(token, classId)
                .map(schoolClass -> ClassDto.builder()
                        .id(schoolClass.getClassId())
                        .name(schoolClass.getName())
                        .build())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{classId}/modules")
    public ResponseEntity<List<ClassModuleDto>> getClassModules(OAuth2AuthenticationToken token,
                                                                @PathVariable UUID classId)
            throws SchoolClassNotFoundException, AuthorizationException {
        return ResponseEntity.ok(this.classService.findClassModules(token, classId)
                .stream()
                .map(classModule -> ClassModuleDto.builder()
                        .id(classModule.getId())
                        .startDate(simpleDateFormat.format(classModule.getStartDate()))
                        .data(ModuleDto.builder()
                                .id(classModule.getModule().getId())
                                .name(classModule.getModule().getName())
                                .description(classModule.getModule().getDescription())
                                .build())
                        .build()
                )
                .toList());
    }
}