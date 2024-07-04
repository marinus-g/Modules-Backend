package academy.mischok.modules.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
@Entity
@Getter
@Setter
public class TeamMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Id
    private String userId;

    @OneToMany(mappedBy = "teamMember")
    private List<ExamMemberEntity> exams;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private TeamEntity team;
}
