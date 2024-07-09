package academy.mischok.modules.repository;

import academy.mischok.modules.model.ClassModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.CallableStatement;
import java.util.List;
import java.util.Optional;

public interface ClassModuleRepository extends JpaRepository<ClassModuleEntity, Long> {

    List<ClassModuleEntity> findByModuleId(Long id);
}
