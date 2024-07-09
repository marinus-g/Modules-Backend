package academy.mischok.modules.service.impl;

import academy.mischok.modules.dtos.*;
import academy.mischok.modules.model.*;

import academy.mischok.modules.repository.*;
import academy.mischok.modules.service.ModuleViewService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<ModuleOverviewDto> getModuleOverviewByUserId(Long userId){
        List<TeamMemberEntity> teamMembers = teamMemberRepository.findByUserId(userId.toString());
        List<ModuleOverviewDto> moduleOverview = new ArrayList<>();

        for(TeamMemberEntity teamMember : teamMembers) {
            TeamEntity team = teamMember.getTeam();
            if (team != null && team.getProject() != null) {
                ProjectEntity project = team.getProject();
                ModuleEntity module = project.getModule();
                if (module != null) {
                    List<ClassModuleEntity> classModules = classModuleRepository.findByModuleId(module.getId());
                    for (ClassModuleEntity classModule : classModules) {
                        List<ExamMemberEntity> examMembers = examMemberRepository.findByTeamMemberId(teamMember.getId());
                        for (ExamMemberEntity examMember : examMembers) {
                            ModuleOverviewDto moduleOverviewDto = new ModuleOverviewDto(
                                    module.getName(),
                                    module.getDescription(),
                                    classModule.getModuleDate(),
                                    project.getProjectGrade(),
                                    examMember.getExamGrade()
                            );
                            moduleOverview.add(moduleOverviewDto);
                        }
                    }
                }
            }
        }
        return moduleOverview;
    }
}
