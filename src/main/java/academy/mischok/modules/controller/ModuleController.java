package academy.mischok.modules.controller;

import academy.mischok.modules.dto.ModuleDto;
import academy.mischok.modules.exception.ModuleNotFoundException;
import academy.mischok.modules.exception.ModuleWithNameAlreadyExistsException;
import academy.mischok.modules.model.Module;
import academy.mischok.modules.service.ModuleService;
import com.azure.core.annotation.Delete;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/module")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    public ResponseEntity<Void> createModule(@RequestBody ModuleDto moduleDto) throws ModuleWithNameAlreadyExistsException {
        return ResponseEntity.created(URI.create(String.format("/module/%s",
                this.moduleService.createModule(moduleDto).getId()))).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuleDto> getModule(@PathVariable Long id) {
        return this.moduleService.findModuleById(id)
                .map(module -> ModuleDto.builder()
                        .id(module.getId())
                        .name(module.getName())
                        .description(module.getDescription())
                        .build())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable Long id) throws ModuleNotFoundException {
        this.moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ModuleDto>> getModules(@RequestParam(required = false) String name) {
        final List<Module> list = name == null ? this.moduleService.findModules() : this.moduleService.findModulesByNameLike(name);
        return ResponseEntity.ok(list
                .stream()
                .map(module -> ModuleDto.builder()
                        .id(module.getId())
                        .name(module.getName())
                        .description(module.getDescription())
                        .build())
                .toList());
    }
}