package academy.mischok.modules.modulbewertung.repository;


import academy.mischok.modules.modulbewertung.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleRepository extends JpaRepository<Module, Long> {
}
