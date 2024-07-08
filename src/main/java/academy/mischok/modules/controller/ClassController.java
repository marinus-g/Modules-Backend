package academy.mischok.modules.controller;

import academy.mischok.modules.dto.ClassDto;
import academy.mischok.modules.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/class")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_Dozentenkollegium')")
    public ResponseEntity<List<ClassDto>> getClasses() {
        return ResponseEntity.ok(this.classService
                .findClasses()
                .stream().map(
                        schoolClass -> ClassDto.builder()
                                .id(schoolClass.getId())
                                .name(schoolClass.getName())
                                .build())
                .toList());
    }

    @GetMapping("/me")
    public ResponseEntity<ClassDto> getClass(OAuth2AuthenticationToken token) {
        return this.classService.findClassByUser(token)
                .map(schoolClass -> ClassDto.builder()
                        .id(schoolClass.getId())
                        .name(schoolClass.getName())
                        .build())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}