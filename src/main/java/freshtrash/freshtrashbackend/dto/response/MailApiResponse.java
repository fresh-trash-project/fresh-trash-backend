package freshtrash.freshtrashbackend.dto.response;

public record MailApiResponse<T>(T data) {
    public static <T> MailApiResponse<T> of(T data) {
        return new MailApiResponse<>(data);
    }
}
