package freshtrash.freshtrashbackend.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import freshtrash.freshtrashbackend.dto.response.ExceptionResponse;
import freshtrash.freshtrashbackend.dto.response.FieldErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * bindingResult 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> resolveException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException is occurred.", e);
        String message = "Validation failed for argument in "
                + e.getParameter().getExecutable().getName();
        return ResponseEntity.status(BAD_REQUEST)
                .body(ExceptionResponse.fromBindingResult(message, e.getBindingResult()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ExceptionResponse> resolveException(BindException e) {
        log.error("BindException is occurred.", e);
        String message = "Validation failed for argument in " + e.getObjectName();
        return ResponseEntity.status(BAD_REQUEST)
                .body(ExceptionResponse.fromBindingResult(message, e.getBindingResult()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> resolveException(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException is occurred.", e);
        return ResponseEntity.status(BAD_REQUEST).body(ExceptionResponse.of(e.getMessage()));
    }

    /**
     * Request 요청 처리시 Enum 클래스와 같은 타입의 데이터 바인딩에 문제가 발생했을 경우
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> resolveException(InvalidFormatException e) {
        log.error("InvalidFormatException is occurred.", e);
        FieldErrorInfo fieldErrorInfo = FieldErrorInfo.of(
                e.getTargetType().getName(),
                String.format("%s가 입력되었습니다.", e.getValue().toString()));
        return ResponseEntity.status(BAD_REQUEST)
                .body(ExceptionResponse.of(e.getOriginalMessage(), List.of(fieldErrorInfo)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> resolveException(Exception e) {
        log.error("Exception is occurred.", e);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(ExceptionResponse.of(e.getMessage()));
    }

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<ExceptionResponse> resolveException(CustomException e) {
        log.error("CustomException is occurred.", e);
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(ExceptionResponse.of(e.getErrorCode().getMessage()));
    }
}
