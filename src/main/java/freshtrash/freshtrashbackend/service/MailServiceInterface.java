package freshtrash.freshtrashbackend.service;

public interface MailServiceInterface {
    void sendMailWithCode(String email, String subject, String text);

    boolean verifyEmailCode(String email, String code);
}
