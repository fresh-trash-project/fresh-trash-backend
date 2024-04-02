package freshtrash.freshtrashbackend.exception.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // File
    INVALID_FIlE_NAME(HttpStatus.BAD_REQUEST, "파일 형식이 잘못되었습니다."),
    FILE_CANT_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),
    FILE_CANT_SAVE(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 저장 할 수 없습니다. 파일 경로를 다시 확인해주세요."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),
    FAILED_DOWNLOAD_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드에 실패했습니다.");

    private final HttpStatus status;
    private final String message;
}
