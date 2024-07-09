package academy.mischok.modules.exception;

public class ModuleNotFoundException extends Exception {

    public ModuleNotFoundException(String message) {
        super(message);
    }

    public ModuleNotFoundException(Long moduleId) {
        super(String.format("Module with id %d not found", moduleId));
    }
}
