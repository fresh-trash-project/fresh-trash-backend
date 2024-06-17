package freshtrash.freshtrashbackend.exception;

import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import lombok.Getter;

@Getter
public class BiddingHistoryException extends CustomException {
    public BiddingHistoryException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BiddingHistoryException(ErrorCode errorCode, Exception causeException) {
        super(errorCode, causeException);
    }
}
