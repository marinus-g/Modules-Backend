package academy.mischok.modules.model.project;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;


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

    @ElementCollection
    @CollectionTable(name = "team_members", joinColumns = @JoinColumn(name = "team_id"))
    private List<UUID> teamMembers;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

}