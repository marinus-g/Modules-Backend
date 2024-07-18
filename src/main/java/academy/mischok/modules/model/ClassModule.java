package academy.mischok.modules.model;

import academy.mischok.modules.model.project.Project;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "class_module")
@Getter
@Setter
@Builder
public class ClassModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @ManyToOne(optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(name = "school_class")
    private UUID schoolClass;

    @Column(name = "start_date")
    private Timestamp startDate;
    @OneToOne
    private Exam exam;

    @OneToOne
    private Project project;

}