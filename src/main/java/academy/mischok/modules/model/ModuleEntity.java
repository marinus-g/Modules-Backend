package academy.mischok.modules.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@RequiredArgsConstructor
@Entity
@Getter
@Setter
public class ModuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true)
    private String name;

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "module")
    private List<ClassModuleEntity> classModules;
}
