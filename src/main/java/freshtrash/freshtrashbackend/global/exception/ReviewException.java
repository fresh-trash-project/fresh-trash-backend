package freshtrash.freshtrashbackend.global.exception;

import freshtrash.freshtrashbackend.global.exception.constants.ErrorCode;
import lombok.Getter;

@Getter
public class ReviewException extends CustomException {
    public ReviewException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ReviewException(ErrorCode errorCode, Exception causeException) {
        super(errorCode, causeException);
    }
}
