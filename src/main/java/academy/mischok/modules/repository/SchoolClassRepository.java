package academy.mischok.modules.repository;

import academy.mischok.modules.model.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {


    Optional<SchoolClass> findByClassId(String classId);

    List<SchoolClass> findByNameStartsWithIgnoreCase(@NonNull String name);



}