package academy.mischok.modules.repository;

import academy.mischok.modules.model.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, Long> {


    boolean existsByModule_Module_IdAndModule_SchoolClass(Long id, UUID schoolClass);

}
