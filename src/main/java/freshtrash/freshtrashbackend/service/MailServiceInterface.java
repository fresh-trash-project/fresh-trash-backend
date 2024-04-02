package freshtrash.freshtrashbackend.service;

public interface MailServiceInterface {
    void sendMailWithCode(String email, String subject, String text);
}
