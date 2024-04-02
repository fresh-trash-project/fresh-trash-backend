package freshtrash.freshtrashbackend.exception;

import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import lombok.Getter;

@Getter
public class MailException extends CustomException {
    public MailException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MailException(ErrorCode errorCode, Exception causeException) {
        super(errorCode, causeException);
    }
}
