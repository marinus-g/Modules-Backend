package academy.mischok.modules.repository;


import academy.mischok.modules.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleRepository extends JpaRepository<Module, Long> {
}
