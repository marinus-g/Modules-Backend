package academy.mischok.modules.service;


import academy.mischok.modules.dtos.ModuleDto;

import java.util.List;
import java.util.Optional;

public interface ModuleViewService {

    Optional<ModuleDto> getModuleById(Long id);

}
