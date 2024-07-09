package academy.mischok.modules.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.util.Date;

@Value
@Builder
public class ClassModuleDto {

    @JsonProperty(value = "class_id")
    Long classId;
    @JsonProperty(value = "start_date")
    String startDate;
    @JsonProperty(value = "data")
    ModuleDto data;

}
