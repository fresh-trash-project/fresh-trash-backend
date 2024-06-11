package freshtrash.freshtrashbackend.exception.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // File
    INVALID_FIlE_NAME(HttpStatus.BAD_REQUEST, "파일 형식이 잘못되었습니다."),
    FILE_CANT_SAVE(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 저장 할 수 없습니다. 파일 경로를 다시 확인해주세요."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),
    INVALID_FIlE(HttpStatus.BAD_REQUEST, "파일 형식이 잘못되었거나 파일이 비어있습니다"),

    // Product
    EMPTY_ADDRESS(HttpStatus.BAD_REQUEST, "주소가 입력되지 않았습니다."),
    NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "폐기물 정보가 존재하지 않습니다."),
    FORBIDDEN_PRODUCT(HttpStatus.FORBIDDEN, "폐기물에 대한 권한이 없습니다."),
    OWNER_PRODUCT_CANT_LIKE(HttpStatus.BAD_REQUEST, "본인 폐기물에는 관심표시할 수 없습니다."),
    ALREADY_EXISTS_LIKE(HttpStatus.BAD_REQUEST, "이미 관심표시한 상태입니다."),
    NOT_FOUND_LIKE(HttpStatus.NOT_FOUND, "관심표시가 존재하지 않습니다."),

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
    UNMATCHED_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    // Alarm
    ALARM_CONNECT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알람을 위한 연결 시도 실패"),
    FORBIDDEN_ALARM(HttpStatus.FORBIDDEN, "알람에 대한 권한이 없습니다."),
    FAILED_SEND_ACK_TO_BROKER(HttpStatus.INTERNAL_SERVER_ERROR, "메시지 브로커로 ack를 전송하는데 실패했습니다."),

    // Chat
    NOT_FOUND_CHAT_ROOM(HttpStatus.NOT_FOUND, "채팅방이 존재하지 않습니다."),
    FORBIDDEN_CHAT_ROOM(HttpStatus.FORBIDDEN, "채팅방에 대한 권한이 없습니다."),
    CANNOT_CHAT_WITH_SELF(HttpStatus.BAD_REQUEST, "판매자는 자신이 등록한 폐기물에 대해 채팅을 시작할 수 없습니다."),

    // Auction
    NOT_FOUND_AUCTION(HttpStatus.NOT_FOUND, "경매가 존재하지 않습니다."),
    FORBIDDEN_AUCTION(HttpStatus.FORBIDDEN, "경매에 대한 권한이 없습니다."),
    INVALID_AUCTION_TIME(HttpStatus.BAD_REQUEST, "경매 시간이 잘못되었습니다."),
    WRITER_CANT_BIDDING(HttpStatus.BAD_REQUEST, "경매 등록 사용자는 입찰할 수 없습니다."),
    INVALID_BIDDING_PRICE(HttpStatus.BAD_REQUEST, "요청 입찰가는 기존 입찰가보다 커야하고 최소 10원 단위의 금액을 입력해야합니다."),
    CANT_BIDDING_TIME(HttpStatus.BAD_REQUEST, "지금은 경매 중이 아닙니다.");

    private final HttpStatus status;
    private final String message;
}
