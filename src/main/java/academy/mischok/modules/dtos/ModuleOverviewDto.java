package academy.mischok.modules.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ModuleOverviewDto {
    private String moduleName;
    private String moduleDescription;
    private String classModuleDate;
    private String examMemberGrade;
    private String projectGrade;
}
