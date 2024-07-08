package academy.mischok.modules.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ModuleDto {

    private String name;
    private String description;
    private ClassModuleDto classModule;
    private List<ExamMemberDto> examMember;
    private ProjectDto project;
}
