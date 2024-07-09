package academy.mischok.modules.exception;

public class SchoolClassNotFoundException extends Exception {

    public SchoolClassNotFoundException(Long id) {
        super(String.format("School class with id %d not found", id));
    }
}
