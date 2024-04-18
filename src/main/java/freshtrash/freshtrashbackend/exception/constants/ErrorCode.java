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
    FAILED_DOWNLOAD_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드에 실패했습니다."),
    INVALID_FIlE(HttpStatus.BAD_REQUEST, "파일 형식이 잘못되었거나 파일이 비어있습니다"),

    // Waste
    EMPTY_ADDRESS(HttpStatus.BAD_REQUEST, "주소가 입력되지 않았습니다."),
    NOT_FOUND_WASTE(HttpStatus.NOT_FOUND, "폐기물 정보가 존재하지 않습니다."),
    FORBIDDEN_WASTE(HttpStatus.FORBIDDEN, "폐기물에 대한 권한이 없습니다."),
    OWNER_WASTE_CANT_LIKE(HttpStatus.BAD_REQUEST, "본인 폐기물에는 관심표시할 수 없습니다."),
    ALREADY_EXISTS_LIKE(HttpStatus.BAD_REQUEST, "이미 관심표시한 상태입니다."),
    NOT_EXISTS_LIKE(HttpStatus.BAD_REQUEST, "관심표시가 존재하지 않습니다."),

    // Review
    ALREADY_EXISTS_REVIEW(HttpStatus.BAD_REQUEST, "이미 리뷰가 등록되었습니다."),

    // Mail
    AUTH_CODE_UNMATCHED(HttpStatus.BAD_REQUEST, "잘못된 인증코드입니다."),
    NOT_FOUND_AUTH_CODE(HttpStatus.NOT_FOUND, "인증코드가 만료되었거나 존재하지 않습니다."),
    EMPTY_AUTH_CODE(HttpStatus.BAD_REQUEST, "인증코드가 입력되지 않았습니다."),
    MAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "메일전송에 실패했습니다"),
    MAIL_VALIDATION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "메일검증에 실패했습니다."),
    MAIL_NOT_VALID(HttpStatus.BAD_REQUEST, "유효하지 않은 메일입니다."),

    // Member
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "유저 정보가 존재하지 않습니다."),
    ALREADY_EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    ALREADY_EXISTS_NICKNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다."),

    // Alarm
    ALARM_CONNECT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알람을 위한 연결 시도 실패"),
    FORBIDDEN_ALARM(HttpStatus.FORBIDDEN, "알람에 대한 권한이 없습니다."),

    // Chat
    NOT_FOUND_CHAT_ROOM(HttpStatus.NOT_FOUND, "채팅방이 존재하지 않습니다."),
    FORBIDDEN_CHAT_ROOM(HttpStatus.FORBIDDEN, "채팅방에 대한 권한이 없습니다.");

    private final HttpStatus status;
    private final String message;
}
