package academy.mischok.modules.service.impl;

import academy.mischok.modules.dto.ModuleDto;
import academy.mischok.modules.exception.ModuleNotFoundException;
import academy.mischok.modules.exception.ModuleWithNameAlreadyExistsException;
import academy.mischok.modules.model.ClassModule;
import academy.mischok.modules.model.Module;
import academy.mischok.modules.repository.ClassModuleRepository;
import academy.mischok.modules.repository.ExamResultRepository;
import academy.mischok.modules.repository.ModuleRepository;
import academy.mischok.modules.repository.ProjectRepository;
import academy.mischok.modules.service.ModuleService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service

@RequiredArgsConstructor
@Getter
@Setter
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    private final ClassModuleRepository classModuleRepository;
    private final ExamResultRepository examMemberRepository;
    private final ProjectRepository projectRepository;

    @Override
    public Module createModule(ModuleDto moduleDto) throws ModuleWithNameAlreadyExistsException {
        if (moduleRepository.existsByNameIgnoreCase(moduleDto.getName())) {
            throw new ModuleWithNameAlreadyExistsException(moduleDto.getName());
        }
        return this.moduleRepository
                .save(Module
                        .builder()
                        .name(moduleDto.getName())
                        .description(moduleDto.getDescription())
                        .classes(new ArrayList<>())
                        .build());
    }

    @Override
    public List<Module> findModulesByNameLike(String name){
        return this.moduleRepository
                .findByNameStartsWithIgnoreCase(name);
    }

    @Override
    public List<Module> findModules() {
        return this.moduleRepository.findAll();
    }

    @Override
    public Optional<Module> findModuleById(Long id) {
        return this.moduleRepository.findById(id);
    }

    @Override
    public void deleteModule(Long id) throws ModuleNotFoundException {
        if (!this.moduleRepository.existsById(id)) {
            throw new ModuleNotFoundException(id);
        }
        this.moduleRepository.deleteById(id);
    }

    @Override
    public Optional<ClassModule> findClassModuleById(Long moduleId) {
        return this.classModuleRepository.findById(moduleId);
    }
}
