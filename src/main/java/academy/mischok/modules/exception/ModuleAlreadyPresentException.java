package academy.mischok.modules.exception;

public class ModuleAlreadyPresentException extends Exception {

    public ModuleAlreadyPresentException(Long id, String module) {
        super(String.format("Module %s with id %s is already present in class", module, id));
    }
}
