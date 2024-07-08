package academy.mischok.modules.repository;

import academy.mischok.modules.model.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long>
{
}
