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
public class ExamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String max_score;

    @ManyToOne
    @JoinColumn(name = "class_module_id")
    private ClassModuleEntity classModule;

    @OneToMany
    private List<ExamMemberEntity> examMembers;


}
