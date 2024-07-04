package academy.mischok.modules.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class ModuleOverviewDto {
    private String moduleName;
    private String moduleDescription;
    private Date moduleDate;
    private String examGrade;
    private String projectGrade;
}
