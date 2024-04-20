package freshtrash.freshtrashbackend.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import freshtrash.freshtrashbackend.dto.cache.EmailCodeCache;
import freshtrash.freshtrashbackend.dto.properties.MailProperties;
import freshtrash.freshtrashbackend.exception.MailException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.EmailCodeCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;
    private final EmailCodeCacheRepository emailCodeCacheRepository;

    @Async
    public void sendMailWithCode(String email, String subject, String code) {
        String text = "fresh-trash 메일 인증 코드입니다. <br/>인증코드:" + code;
        sendMail(email, subject, text);
        emailCodeCacheRepository.save(EmailCodeCache.of(email, code));
        log.debug("--reids에 code 저장");
    }

    public EmailCodeCache getEmailCodeCache(String email) {
        return emailCodeCacheRepository
                .findById(email)
                .orElseThrow(() -> new MailException(ErrorCode.NOT_FOUND_AUTH_CODE));
    }

    public void verifyEmailCode(String email, String code) {
        EmailCodeCache emailCodeCache = getEmailCodeCache(email);
        if (!StringUtils.hasText(code)) {
            throw new MailException(ErrorCode.EMPTY_AUTH_CODE);
        }

        if (!emailCodeCache.code().equals(code)) {
            throw new MailException(ErrorCode.AUTH_CODE_UNMATCHED);
        }
    }

    public void sendMail(String toMail, String subject, String text) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(toMail);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new MailException(ErrorCode.MAIL_SEND_FAIL, e);
        }
    }

    /**
     * 메일 유효성 검증
     */
    public void isValidMail(String email) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://emailvalidation.abstractapi.com/v1/?api_key=" + mailProperties.apiKey()
                        + "&email=" + email))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String result = response.body();
            JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
            String deliverable = jsonObject.get("deliverability").getAsString();

            boolean isFree = jsonObject
                    .get("is_free_email")
                    .getAsJsonObject()
                    .get("value")
                    .getAsBoolean();

            if (!deliverable.equals("DELIVERABLE") || !isFree) {
                throw new MailException(ErrorCode.MAIL_NOT_VALID);
            }

        } catch (IOException | InterruptedException e) {
            throw new MailException(ErrorCode.MAIL_VALIDATION_FAIL, e);
        }
    }
}
