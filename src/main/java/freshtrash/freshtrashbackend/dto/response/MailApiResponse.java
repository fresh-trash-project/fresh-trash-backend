package freshtrash.freshtrashbackend.dto.response;

public record MailApiResponse(String data) {
    public static MailApiResponse of(String data) {
        return new MailApiResponse(data);
    }
}
