package freshtrash.freshtrashbackend.exception;

import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import lombok.Getter;

@Getter
public class AlarmException extends CustomException {
    public AlarmException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AlarmException(ErrorCode errorCode, Exception causeException) {
        super(errorCode, causeException);
    }
}
