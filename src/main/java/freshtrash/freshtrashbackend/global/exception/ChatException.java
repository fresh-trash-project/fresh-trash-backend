package freshtrash.freshtrashbackend.global.exception;

import freshtrash.freshtrashbackend.global.exception.constants.ErrorCode;
import lombok.Getter;

@Getter
public class ChatException extends CustomException {
    public ChatException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChatException(ErrorCode errorCode, Exception causeException) {
        super(errorCode, causeException);
    }
}
