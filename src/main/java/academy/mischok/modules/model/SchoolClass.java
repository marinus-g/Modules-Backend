package academy.mischok.modules.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public final class SchoolClass {


    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    @Column(name = "class_id")
    private String classId;
    @Column(name = "name")
    private String name;


    @OneToMany(mappedBy = "schoolClass")
    private List<ClassModule> modules;

    public SchoolClass(String classId, String name) {
        this.classId = classId;
        this.name = name;
    }
}