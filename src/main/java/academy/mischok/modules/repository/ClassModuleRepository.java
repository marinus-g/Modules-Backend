package academy.mischok.modules.repository;

import academy.mischok.modules.model.ClassModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClassModuleRepository extends JpaRepository<ClassModule, Long> {
    List<ClassModule> findBySchoolClass(UUID schoolClass);

    Optional<ClassModule> findBySchoolClassAndModule_Id(UUID schoolClass, Long id);


}
