package academy.mischok.modules.repository;

import academy.mischok.modules.model.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long>
{
}
