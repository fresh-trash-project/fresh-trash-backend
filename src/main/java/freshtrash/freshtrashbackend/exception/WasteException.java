package freshtrash.freshtrashbackend.exception;

import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import lombok.Getter;

@Getter
public class WasteException extends CustomException {
    public WasteException(ErrorCode errorCode) {
        super(errorCode);
    }

    public WasteException(ErrorCode errorCode, Exception causeException) {
        super(errorCode, causeException);
    }
}
