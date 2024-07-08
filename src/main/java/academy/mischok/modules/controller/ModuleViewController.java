package academy.mischok.modules.controller;

import academy.mischok.modules.dtos.ModuleDto;
import academy.mischok.modules.dtos.ModuleOverviewDto;
import academy.mischok.modules.repository.ClassModuleRepository;
import academy.mischok.modules.service.ModuleViewService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Getter
@Setter
public class ModuleViewController {

    private final ModuleViewService moduleViewService;
    private final ClassModuleRepository classModuleRepository;

    @GetMapping("/modules/{moduleId}")
    public ResponseEntity<List<ModuleOverviewDto>> getModuleOverviewByModuleId(@PathVariable Long moduleId) {
        List<ModuleOverviewDto> modules = moduleViewService.getModuleOverviewByModuleId(moduleId);
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/modules/search/{name}")
    public ResponseEntity<List<ModuleDto>> searchModulesByName(@RequestParam String name) {
        List<ModuleDto> modules = moduleViewService.getModuleByName(name)
                .stream()
                .toList();
        return ResponseEntity.ok(modules);
    }
}
