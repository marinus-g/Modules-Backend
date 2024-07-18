package academy.mischok.modules.service;

import academy.mischok.modules.dto.ProjectDto;
import academy.mischok.modules.exception.AuthorizationException;
import academy.mischok.modules.exception.ModuleAlreadyHasProjectException;
import academy.mischok.modules.exception.ModuleNotFoundException;
import academy.mischok.modules.exception.ProjectNotFoundException;
import academy.mischok.modules.model.project.Project;
import academy.mischok.modules.model.project.Team;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ProjectService {

    Project createProject(ProjectDto project) throws ModuleAlreadyHasProjectException, ModuleNotFoundException;

    Optional<Project> getProject(OAuth2AuthenticationToken token, Long id) throws AuthorizationException;

    Team createProjectTeam(Long projectId, List<UUID> teamMembers) throws ProjectNotFoundException;
}
