package academy.mischok.modules.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class ProjectDto {

    @JsonProperty("id")
    Long id;
    @JsonProperty("name")
    String name;
    @JsonProperty("description")
    String description;
    @JsonProperty("class_id")
    UUID classId;
    @JsonProperty("module_id")
    Long moduleId;

}