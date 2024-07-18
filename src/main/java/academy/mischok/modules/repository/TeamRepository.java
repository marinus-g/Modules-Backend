package academy.mischok.modules.repository;

import academy.mischok.modules.model.project.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByIdAndTeamMembersContaining(Long teamId, UUID userId);

    boolean existsByProject_IdAndTeamMembersContaining(Long projectId, UUID userId);

}
