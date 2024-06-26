package academy.mischok.modules.modulbewertung.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int projectScore;
    private int examScore;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;

    // Getter und Setter

    public double getTotalScore() {
        return (projectScore * 0.5) + (examScore * 0.5);
    }

    public String getGrade() {
        double totalScore = getTotalScore();
        if (totalScore >= 92) return "1";
        if (totalScore >= 81) return "2";
        if (totalScore >= 67) return "3";
        if (totalScore >= 50) return "4";
        if (totalScore >= 30) return "5";
        return "6";
    }
}
