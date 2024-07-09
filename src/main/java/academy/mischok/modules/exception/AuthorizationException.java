package academy.mischok.modules.exception;

import jakarta.security.auth.message.AuthException;

public class AuthorizationException extends AuthException {

    public AuthorizationException(String msg) {
        super(msg);
    }
}
