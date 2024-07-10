package academy.mischok.modules.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ExamResultDto {

    @JsonProperty("score")
    Integer score;
    @JsonProperty("grade")
    Integer grade;
    @JsonProperty("user_id")
    UUID userId;
}
