package freshtrash.freshtrashbackend.global.exception;

import freshtrash.freshtrashbackend.global.exception.constants.ErrorCode;
import lombok.Getter;

@Getter
public class ChatRoomException extends CustomException {
    public ChatRoomException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChatRoomException(ErrorCode errorCode, Exception causeException) {
        super(errorCode, causeException);
    }
}
