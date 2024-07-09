package academy.mischok.modules.repository;

import academy.mischok.modules.model.TeamMemberEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository {


    List<TeamMemberEntity> findByUserId(String string);
}
