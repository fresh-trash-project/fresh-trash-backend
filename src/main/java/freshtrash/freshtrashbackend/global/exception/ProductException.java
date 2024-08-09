package freshtrash.freshtrashbackend.global.exception;

import freshtrash.freshtrashbackend.global.exception.constants.ErrorCode;
import lombok.Getter;

@Getter
public class ProductException extends CustomException {
    public ProductException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ProductException(ErrorCode errorCode, Exception causeException) {
        super(errorCode, causeException);
    }
}
