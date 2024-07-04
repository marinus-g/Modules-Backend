package academy.mischok.modules.repository;

import academy.mischok.modules.dtos.ModuleOverviewDto;
import academy.mischok.modules.model.ModuleEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity, Long> {

    @Query("SELECT new academy.mischok.modules.dtos.ModuleOverviewDto(m.name, m.description, cm.date, em.grade, p.grade) " +
    "FROM ModuleEntity m " +
    "JOIN m.classModules cm " +
    "JOIN cm.exams e " +
    "JOIN e.examMembers em " +
    "JOIN m.projects p " +
    "WHERE m.id = :moduleId")
    List<ModuleOverviewDto> findModuleOverviewByModuleId(Long moduleId);
}
