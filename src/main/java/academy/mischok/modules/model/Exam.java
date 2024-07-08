package academy.mischok.modules.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Builder
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "max_score")
    private Integer maxScore;

    @ManyToOne
    private ClassModule classModule;

    @OneToMany
    private List<ExamResult> examMember;
}
