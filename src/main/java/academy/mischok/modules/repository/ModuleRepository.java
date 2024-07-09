package academy.mischok.modules.repository;

import academy.mischok.modules.dtos.ModuleOverviewDto;
import academy.mischok.modules.model.ModuleEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity, Long> {

    List<ModuleEntity> findByNameContaining(String name);
}
