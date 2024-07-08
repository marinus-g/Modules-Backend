package academy.mischok.modules.controller.advise;

import academy.mischok.modules.exception.ModuleNotFoundException;
import academy.mischok.modules.exception.ModuleWithNameAlreadyExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(ModuleWithNameAlreadyExistsException.class)
    public ResponseEntity<String> handleModuleWithNameAlreadyExistsException(ModuleWithNameAlreadyExistsException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(ModuleNotFoundException.class)
    public ResponseEntity<String> handleModuleNotFoundException(ModuleNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        return ResponseEntity.status(500).body(exception.getMessage());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<String> handleAuthorizationDeniedException(AuthorizationDeniedException exception) {
        return ResponseEntity.status(403).body(exception.getMessage());
    }
}