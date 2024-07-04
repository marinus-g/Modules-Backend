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

    @Query("SELECT new academy.mischok.modules.dtos.ModuleOverviewDto(m.name, m.description, cm.moduleDate, em.examGrade, p.projectGrade) " +
    "FROM ModuleEntity m " +
    "JOIN m.classModules cm " +
    "JOIN cm.exams e " +
    "JOIN e.examMember em " +
    "JOIN ProjectEntity p ON p.module = m " +
    "WHERE m.id = :moduleId")
    List<ModuleOverviewDto> findModuleOverviewByModuleId(Long moduleId);

    Optional<ModuleEntity> findByName(String name);
}
