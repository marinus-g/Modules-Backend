package academy.mischok.modules;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@Entity
@Table(name = "Quiz")
@AllArgsConstructor
@NoArgsConstructor
public class QuizExcelEntity {
    @Id
    private String fullName;
    private String firstName;
    private String lastName;
    private String email;
    private String module;
    private LocalDate dueToDate;
    private LocalDate evaluationDate;
    private String turnedIn;
    private String feedback;
    private int achievedPoints;
    private int maxPoints;
    private double percent;

}
