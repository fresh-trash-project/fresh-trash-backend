package freshtrash.freshtrashbackend.service.impl;

import freshtrash.freshtrashbackend.exception.MailException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final RedisService redisService;
    private final int AUTH_CODE_DURATION_MIN = 10;
    private final int AUTH_SUCCESS_DURATION_MIN = 300;
    private final String AUTH_SUCCESS = "AuthSuccess";

    public void sendMailWithCode(String email, String subject, String code) {
        String text = "fresh-trash 메일 인증 코드입니다. <br/>인증코드:" + code;
        sendMail(email, subject, text);
        redisService.saveEmailVerificationCode(email, code, AUTH_CODE_DURATION_MIN);
        log.debug("--reids에 code 저장");
    }

    public boolean verifyEmailCode(String email, String code) {
        String authCode = redisService.getData(email);
        if (!StringUtils.hasText(code)) {
            throw new MailException(ErrorCode.EMPTY_AUTH_CODE);
        }

        if (!authCode.equals(code)) {
            throw new MailException(ErrorCode.AUTH_CODE_UNMATCHED);
        }

        // 인증완료 redis 저장
        redisService.saveEmailVerificationCode(email, AUTH_SUCCESS, AUTH_SUCCESS_DURATION_MIN);
        return true;
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
}
