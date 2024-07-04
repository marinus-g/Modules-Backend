package academy.mischok.modules.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String score;

    private String projectGrade;

    @OneToMany(mappedBy = "project")
    private List<TeamEntity> teams;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private ModuleEntity module;
}
