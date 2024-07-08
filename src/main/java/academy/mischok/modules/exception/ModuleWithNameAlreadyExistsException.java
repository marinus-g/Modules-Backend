package academy.mischok.modules.exception;

public class ModuleWithNameAlreadyExistsException extends Exception {

    public ModuleWithNameAlreadyExistsException(String moduleName) {
        super(String.format("Module with name %s already exists", moduleName));
    }
}
