package freshtrash.freshtrashbackend.dto.response;

public record FieldErrorInfo(String field, String message) {
    public static FieldErrorInfo of(String field, String message) {
        return new FieldErrorInfo(field, message);
    }
}
