package academy.mischok.modules.service;

import academy.mischok.modules.model.OAuthUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    boolean existsByEmail(UUID group, String email);

    Optional<UUID> findIdByEmail(UUID group, String email);

    Optional<OAuthUser> findUserById(UUID userId);

    List<OAuthUser> findUsersInGroup(UUID group);

}
