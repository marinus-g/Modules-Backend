package academy.mischok.modules.model;

import lombok.*;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
public final class SchoolClass {

    private UUID classId;
    private String name;

}