package academy.mischok.modules.exception;

import java.util.UUID;

public class SchoolClassNotFoundException extends Exception {

    public SchoolClassNotFoundException(UUID uuid) {
        super(String.format("School class with uuid %s not found", uuid));
    }
}
