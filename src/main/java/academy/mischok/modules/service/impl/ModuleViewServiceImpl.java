package academy.mischok.modules.service.impl;

import academy.mischok.modules.dtos.*;
import academy.mischok.modules.model.ModuleEntity;
import academy.mischok.modules.repository.ClassModuleRepository;
import academy.mischok.modules.repository.ExamMemberRepository;
import academy.mischok.modules.repository.ModuleRepository;
import academy.mischok.modules.repository.ProjectRepository;
import academy.mischok.modules.service.ModuleViewService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@RequiredArgsConstructor
@Service
public class ModuleViewServiceImpl implements ModuleViewService {

    private final ModuleRepository moduleRepository;
    private final ClassModuleRepository classModuleRepository;
    private final ExamMemberRepository examMemberRepository;
    private final ProjectRepository projectRepository;

    @Override
    public Optional<ModuleDto> getModuleByName(String name){
        Optional<ModuleEntity> moduleEntityOptional = moduleRepository.findByName(name);

        return moduleEntityOptional.map(moduleEntity -> {
            ModuleDto moduleDto = new ModuleDto();
            moduleDto.setName(moduleEntity.getName());
            moduleDto.setDescription(moduleEntity.getDescription());
            return moduleDto;
        });
    }

    @Override
    public List<ModuleOverviewDto> getModuleOverviewByModuleId(Long moduleId){
        Optional<ModuleEntity> moduleEntityOptional = moduleRepository.findById(moduleId);
        if (!moduleEntityOptional.isPresent()) {
            return Collections.emptyList();
        }
        ModuleEntity moduleEntity = moduleEntityOptional.get();
        List<ModuleOverviewDto> moduleOverviews = moduleRepository.findModuleOverviewByModuleId(moduleId);
        return moduleOverviews;
    }
}
