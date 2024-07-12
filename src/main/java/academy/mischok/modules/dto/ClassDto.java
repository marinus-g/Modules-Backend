package academy.mischok.modules.dto;


import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ClassDto {

    UUID id;
    String name;

}
