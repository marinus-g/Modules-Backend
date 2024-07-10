package academy.mischok.modules.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ExamDto {

    @JsonProperty("id")
    Long id;
    @JsonProperty("module_id")
    Long moduleId;

    @JsonProperty("class_id")
    Long classId;

    @JsonProperty("date")
    String date;

    @JsonProperty("max_score")
    Integer maxScore;

    @JsonProperty("exam_results")
    List<ExamResultDto> examResults;
}
