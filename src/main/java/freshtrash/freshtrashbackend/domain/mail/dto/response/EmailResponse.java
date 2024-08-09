package freshtrash.freshtrashbackend.domain.mail.dto.response;

public record EmailResponse(String code) {
    public static EmailResponse of(String code) {
        return new EmailResponse(code);
    }
}
