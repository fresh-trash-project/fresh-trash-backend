package freshtrash.freshtrashbackend.exception;

import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
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
