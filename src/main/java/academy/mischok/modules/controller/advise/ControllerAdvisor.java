package academy.mischok.modules.controller.advise;

import academy.mischok.modules.exception.ModuleAlreadyPresentException;
import academy.mischok.modules.exception.ModuleNotFoundException;
import academy.mischok.modules.exception.ModuleWithNameAlreadyExistsException;
import academy.mischok.modules.exception.SchoolClassNotFoundException;
import okhttp3.OkHttp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(ModuleWithNameAlreadyExistsException.class)
    public ResponseEntity<String> handleModuleWithNameAlreadyExistsException(ModuleWithNameAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
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

    @ExceptionHandler(ModuleAlreadyPresentException.class)
    public ResponseEntity<String> moduleAlreadyPresentInClass(ModuleAlreadyPresentException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(SchoolClassNotFoundException.class)
    public ResponseEntity<String> handleSchoolClassNotFoundException(SchoolClassNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }
}