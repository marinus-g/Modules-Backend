package academy.mischok.modules.service;

import academy.mischok.modules.dtos.ModuleDto;
import academy.mischok.modules.dtos.ModuleOverviewDto;

import java.util.List;
import java.util.Optional;

public interface ModuleViewService {

    Optional<ModuleDto> getModuleByName(String name);

    List<ModuleOverviewDto> getModuleOverviewByModuleId(Long id);
}
