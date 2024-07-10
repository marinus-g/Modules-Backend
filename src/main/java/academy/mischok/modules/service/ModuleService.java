package academy.mischok.modules.service;

import academy.mischok.modules.dto.ModuleDto;
import academy.mischok.modules.exception.ModuleNotFoundException;
import academy.mischok.modules.exception.ModuleWithNameAlreadyExistsException;
import academy.mischok.modules.model.ClassModule;
import academy.mischok.modules.model.Module;

import java.util.List;
import java.util.Optional;

public interface ModuleService {

    Module createModule(ModuleDto moduleDto) throws ModuleWithNameAlreadyExistsException;

    List<Module> findModulesByNameLike(String name);

    List<Module> findModules();

    Optional<Module> findModuleById(Long id);

    void deleteModule(Long id) throws ModuleNotFoundException;

    Optional<ClassModule> findClassModuleById(Long moduleId);
}
