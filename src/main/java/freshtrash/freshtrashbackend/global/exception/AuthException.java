package freshtrash.freshtrashbackend.global.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class AuthException extends AuthenticationException {
    public AuthException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AuthException(String msg) {
        super(msg);
    }
}
