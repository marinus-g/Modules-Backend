package academy.mischok.modules.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Builder
@ToString
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private Date date;

    @Column(name = "max_score")
    private Integer maxScore;

    @OneToOne
    private ClassModule classModule;

    @OneToMany
    @ToString.Exclude
    private List<ExamResult> examResults;
}
