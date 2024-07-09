package academy.mischok.modules.service;

import academy.mischok.modules.dtos.ModuleDto;
import academy.mischok.modules.dtos.ModuleOverviewDto;

import java.util.List;

public interface ModuleViewService {

    List<ModuleDto> getModuleByName(String name);

    List<ModuleOverviewDto> getModuleOverviewByUserId(Long userId);
}
