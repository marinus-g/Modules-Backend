package academy.mischok.modules.modulbewertung.controller;

import academy.mischok.modules.modulbewertung.model.Module;
import academy.mischok.modules.modulbewertung.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @GetMapping
    public List<Module> getAllModules() {
        return moduleService.getAllModules();
    }

    @GetMapping("/{id}")
    public Optional<Module> getModule(@PathVariable Long id) {
        return moduleService.getModule(id);
    }

    @PostMapping
    public Module saveModule(@RequestBody Module module) {
        return moduleService.saveModule(module);
    }

    @DeleteMapping("/{id}")
    public void deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
    }
}
