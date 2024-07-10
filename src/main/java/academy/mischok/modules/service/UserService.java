package academy.mischok.modules.service;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    boolean existsByEmail(String email);

    Optional<UUID> findIdByEmail(String email);

}
