package academy.mischok.modules.service.impl;

import academy.mischok.modules.dto.ProjectDto;
import academy.mischok.modules.exception.*;
import academy.mischok.modules.model.ClassModule;
import academy.mischok.modules.model.SchoolClass;
import academy.mischok.modules.model.project.Project;
import academy.mischok.modules.model.project.Team;
import academy.mischok.modules.repository.ClassModuleRepository;
import academy.mischok.modules.repository.ProjectRepository;
import academy.mischok.modules.repository.TeamRepository;
import academy.mischok.modules.service.ClassService;
import academy.mischok.modules.service.ProjectService;
import academy.mischok.modules.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final ClassModuleRepository classModuleRepository;
    private final ClassService classService;
    private final UserService userService;

    @Override
    public Project createProject(ProjectDto project) throws ModuleAlreadyHasProjectException, ModuleNotFoundException {
        if (projectRepository.existsByModule_Module_IdAndModule_SchoolClass(project.getModuleId(),
                project.getClassId())) {
            throw new ModuleAlreadyHasProjectException("Module already has a project");
        }
        final ClassModule classModule = classModuleRepository.findBySchoolClassAndModule_Id(project.getClassId(),
                project.getModuleId()).orElseThrow(() -> new ModuleNotFoundException("Module not found"));

        Project result = projectRepository.save(Project.builder()
                .name(project.getName())
                .description(project.getDescription())
                .module(classModule)
                .build());
        classModule.setProject(result);
        classModuleRepository.save(classModule);
        return result;
    }

    @Override
    public Optional<Project> getProject(OAuth2AuthenticationToken token, Long id) throws AuthorizationException {

        Optional<Project> optional = projectRepository.findById(id);
        if (optional.isEmpty()) {
            return optional;
        }
        final Project project = optional.get();
        final SchoolClass schoolClass = classService.findClassById(token, project.getModule().getSchoolClass())
                .orElseThrow(() -> new AuthorizationException("User is not authorized to access this project"));
        final String className = schoolClass.getName();
        if (token.getAuthorities().stream().noneMatch(grantedAuthority
                -> grantedAuthority.getAuthority().equals("ROLE_" + className) || grantedAuthority.getAuthority().equals("ROLE_Dozentenkollegium"))) {
            throw new AuthorizationException("Not authorized to access this class");
        }
        return optional;
    }

    @Override
    public Team createProjectTeam(Long projectId, List<UUID> teamMembers) throws ProjectNotFoundException {
        if (teamMembers.isEmpty()) {
            throw new IllegalArgumentException("Team members cannot be empty");
        }

        final Project project = this.projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException(projectId));
        teamMembers.forEach(uuid -> {
            if (teamRepository.existsByProject_IdAndTeamMembersContaining(projectId, uuid)) {
                throw new IllegalArgumentException("User is already in the team");
            }
            if (userService.findUserById(uuid).isEmpty()) {
                throw new IllegalArgumentException(String.format("User with id %s not found", uuid));
            }
        });
        return teamRepository.save(Team.builder()
                .project(project)
                .teamMembers(teamMembers)
                .build());
    }
}