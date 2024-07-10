package academy.mischok.modules.util;

import academy.mischok.modules.service.UserService;

import java.util.Optional;
import java.util.UUID;

public class DummyUserService implements UserService {
    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public Optional<UUID> findIdByEmail(String email) {
        return Optional.empty();
    }
}
