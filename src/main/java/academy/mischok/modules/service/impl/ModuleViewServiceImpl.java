package academy.mischok.modules.service.impl;

import academy.mischok.modules.dtos.ClassModuleDto;
import academy.mischok.modules.dtos.ExamMemberDto;
import academy.mischok.modules.dtos.ModuleDto;
import academy.mischok.modules.dtos.ProjectDto;
import academy.mischok.modules.model.ModuleEntity;
import academy.mischok.modules.model.ExamMemberEntity;
import academy.mischok.modules.model.ProjectEntity;
import academy.mischok.modules.repository.ClassModuleRepository;
import academy.mischok.modules.repository.ExamMemberRepository;
import academy.mischok.modules.repository.ModuleRepository;
import academy.mischok.modules.repository.ProjectRepository;
import academy.mischok.modules.service.ModuleViewService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

    @Override
    public Optional<ModuleDto> getModuleById(Long id){
        Optional<ModuleEntity> moduleEntityOptional = moduleRepository.findById(id);

        if(moduleEntityOptional.isPresent()) {
            ModuleEntity moduleEntity = moduleEntityOptional.get();
            ModuleDto moduleDto = new ModuleDto();

            moduleDto.setName(moduleEntity.getName());
            moduleDto.setDescription(moduleEntity.getDescription());

            return Optional.of(moduleDto);
        }

        return Optional.empty();
    }


}
