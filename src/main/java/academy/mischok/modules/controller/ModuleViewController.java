package academy.mischok.modules.controller;

import academy.mischok.modules.repository.ClassModuleRepository;
import academy.mischok.modules.service.ModuleViewService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Getter
@Setter
public class ModuleViewController {

    private final ModuleViewService moduleViewService;
    private final ClassModuleRepository classModuleRepository;

    @GetMapping("/modules")
    public List<ModuleData> getModules() {
        return moduleViewService.getAllModules().stream()
                .map(module -> {
                    ModuleData data = new ModuleData();
                    data.setName(module.getName());
                    data.setDescription(module.getDescription());
                    data.setDate(module.getClassModule().getDate());
                    data.setExamGrades(module.getExam().getGrades());
                    data.setProjectGrade(module.getProject().getGrade());
                    return data;
                })
                .collect(Collectors.toList());
    }
}
