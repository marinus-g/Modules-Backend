package academy.mischok.modules.controller;

import academy.mischok.modules.dto.ProjectDto;
import academy.mischok.modules.exception.AuthorizationException;
import academy.mischok.modules.exception.ModuleAlreadyHasProjectException;
import academy.mischok.modules.exception.ModuleNotFoundException;
import academy.mischok.modules.exception.ProjectNotFoundException;
import academy.mischok.modules.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_Dozentenkollegium')")
    public ResponseEntity<?> createProject(@RequestBody ProjectDto projectDto) throws ModuleAlreadyHasProjectException, ModuleNotFoundException {
        return Optional
                .of(projectService.createProject(projectDto))
                .map(project1 -> ResponseEntity.created(URI.create("/project/" + project1.getId())).build())
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProject(OAuth2AuthenticationToken token, @PathVariable Long id) throws AuthorizationException {
        return projectService.getProject(token, id)
                .map(project ->
                    ProjectDto.builder()
                            .id(project.getId())
                            .name(project.getName())
                            .description(project.getDescription())
                            .classId(project.getModule().getSchoolClass())
                            .moduleId(project.getModule().getModule().getId())
                            .build()
                )
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_Dozentenkollegium')")
    public ResponseEntity<?> createProjectTeam(@PathVariable Long projectId, @RequestBody List<UUID> teamMembers) throws ProjectNotFoundException {
        return Optional
                .of(projectService.createProjectTeam(projectId, teamMembers))
                .map(projectTeam -> ResponseEntity.created(URI.create("/project/" + projectId + "/team/" + projectTeam.getId())).build())
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}