package freshtrash.freshtrashbackend.global.exception;

import freshtrash.freshtrashbackend.global.exception.constants.ErrorCode;
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
