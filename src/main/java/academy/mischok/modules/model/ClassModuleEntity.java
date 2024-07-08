package academy.mischok.modules.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Entity
@Getter
@Setter
public class ClassModuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String classId;

    private Date moduleDate;

    @OneToMany(mappedBy = "classModule")
    private List<ExamEntity> exams;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private ModuleEntity module;
}
