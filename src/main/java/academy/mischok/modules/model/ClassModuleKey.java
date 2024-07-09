package academy.mischok.modules.model;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ClassModuleKey {

    private Long schoolClass;
    private Long module;

}
