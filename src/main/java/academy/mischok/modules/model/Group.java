package academy.mischok.modules.model;

import com.azure.core.annotation.Get;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
public class Group {

    private String id;
    private String displayName;

}
