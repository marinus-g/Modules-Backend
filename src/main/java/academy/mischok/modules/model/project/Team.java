package academy.mischok.modules.model.project;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "team")
    private List<TeamMember> teamMembers;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

}