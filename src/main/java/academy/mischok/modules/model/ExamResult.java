package academy.mischok.modules.model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class ExamResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private String userId;

    @Column(name = "score")
    private Integer score;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;
}
