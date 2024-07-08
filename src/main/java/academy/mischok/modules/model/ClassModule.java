package academy.mischok.modules.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "class_module")
@Getter
@Setter
public class ClassModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @ManyToOne(optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;
    @ManyToOne
    private SchoolClass schoolClass;

    @Column(name = "start_date")
    private Timestamp startDate;

}