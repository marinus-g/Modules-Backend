package academy.mischok.modules.repository;

import academy.mischok.modules.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    Optional<Module> findByName(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Module> findByNameStartsWithIgnoreCase(String name);


}
