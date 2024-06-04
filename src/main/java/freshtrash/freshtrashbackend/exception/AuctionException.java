package freshtrash.freshtrashbackend.exception;

import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import lombok.Getter;

@Getter
public class AuctionException extends CustomException {
    public AuctionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuctionException(ErrorCode errorCode, Exception causeException) {
        super(errorCode, causeException);
    }
}
