package academy.mischok.modules.modulbewertung.controller;

import academy.mischok.modules.modulbewertung.model.Module;
import academy.mischok.modules.modulbewertung.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/modules")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @GetMapping
    public String getAllModules(Model model) {
        List<Module> modules = moduleService.getAllModules();
        model.addAttribute("modules", modules);
        return "module/list";
    }

    @GetMapping("/{id}")
    public String getModule(@PathVariable Long id, Model model) {
        Module module = moduleService.getModule(id).orElse(null);
        model.addAttribute("module", module);
        return "module/detail";
    }

    @GetMapping("/new")
    public String newModuleForm(Model model) {
        model.addAttribute("module", new Module());
        return "module/form";
    }

    @PostMapping
    public String saveModule(@ModelAttribute Module module) {
        moduleService.saveModule(module);
        return "redirect:/modules";
    }

    @GetMapping("/delete/{id}")
    public String deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return "redirect:/modules";
    }
}
