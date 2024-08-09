package freshtrash.freshtrashbackend.global.exception;

import freshtrash.freshtrashbackend.global.exception.constants.ErrorCode;
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
