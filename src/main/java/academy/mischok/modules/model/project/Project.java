package academy.mischok.modules.model.project;

import academy.mischok.modules.model.Module;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "score")
    private String score;

    @Column(name = "project_points")
    private Integer projectPoints;

    @OneToMany(mappedBy = "project")
    private List<Team> teams;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;
}
