package academy.mischok.modules.dto;


import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClassDto {

    Long id;
    String name;

}
