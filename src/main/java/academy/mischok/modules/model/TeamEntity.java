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
public class TeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team")
    private List<TeamMemberEntity> teamMembers;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity project;
}
