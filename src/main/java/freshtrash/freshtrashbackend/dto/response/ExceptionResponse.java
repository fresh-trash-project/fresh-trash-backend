package freshtrash.freshtrashbackend.dto.response;

import org.springframework.validation.BindingResult;

import java.util.List;

public record ExceptionResponse(String message, List<FieldErrorInfo> errors) {
    public static ExceptionResponse of(String message) {
        return new ExceptionResponse(message, null);
    }

    public static ExceptionResponse of(String message, List<FieldErrorInfo> errors) {
        return new ExceptionResponse(message, errors);
    }

    public static ExceptionResponse fromBindingResult(String message, BindingResult bindingResult) {
        return new ExceptionResponse(
                message,
                bindingResult.getFieldErrors().stream()
                        .map(fieldError -> FieldErrorInfo.of(fieldError.getField(), fieldError.getDefaultMessage()))
                        .toList());
    }
}
