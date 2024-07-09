package academy.mischok.modules.service.impl;

import academy.mischok.modules.dtos.*;
import academy.mischok.modules.model.*;

import academy.mischok.modules.repository.*;
import academy.mischok.modules.service.ModuleViewService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@RequiredArgsConstructor
@Service
public class ModuleViewServiceImpl implements ModuleViewService {

    private final ModuleRepository moduleRepository;
    private final ClassModuleRepository classModuleRepository;
    private final ExamMemberRepository examMemberRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    public List<ModuleDto> getModuleByName(String name){
        List<ModuleEntity> moduleEntities = moduleRepository.findByNameContaining(name);

        return moduleEntities.stream().map(moduleEntity -> {
            ModuleDto moduleDto = new ModuleDto();
            moduleDto.setName(moduleEntity.getName());
            moduleDto.setDescription(moduleEntity.getDescription());
            return moduleDto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ModuleOverviewDto> getModuleOverviewByUserId(Long userId) {
        return teamMemberRepository.findByUserId(userId.toString()).stream()
                .filter(teamMember -> teamMember.getTeam() != null && teamMember.getTeam().getProject() != null)
                .flatMap(teamMember -> {
                    TeamEntity team = teamMember.getTeam();
                    ProjectEntity project = team.getProject();
                    ModuleEntity module = project.getModule();
                    if (module == null) return Stream.empty();
                    return classModuleRepository.findByModuleId(module.getId()).stream()
                            .flatMap(classModule -> examMemberRepository.findByTeamMemberId(teamMember.getId()).stream()
                                    .map(examMember -> new ModuleOverviewDto(
                                            module.getName(),
                                            module.getDescription(),
                                            classModule.getModuleDate(),
                                            project.getProjectGrade(),
                                            examMember.getExamGrade()
                                    )));
                })
                .collect(Collectors.toList());
    }
}
