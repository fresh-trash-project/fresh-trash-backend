package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.cache.EmailCodeCache;
import freshtrash.freshtrashbackend.exception.MailException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.EmailCodeCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final EmailCodeCacheRepository emailCodeCacheRepository;

    @Async
    @Transactional
    public void sendMailWithCode(String email, String subject, String code) {
        String text = "fresh-trash 메일 인증 코드입니다. <br/>인증코드:" + code;
        emailCodeCacheRepository.save(EmailCodeCache.of(email, code));
        sendMail(email, subject, text);
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
            throw new MailException(ErrorCode.UNMATCHED_AUTH_CODE);
        }
    }

    @Async
    public void sendMailWithTemporaryPassword(String email, String temporaryPassword) {
        String text = "fresh-trash 임시 비밀번호 입니다. <br/>임시 비밀번호: " + temporaryPassword;
        String subject = "fresh-trash 임시 비밀번호 안내";
        sendMail(email, subject, text);
    }

    private void sendMail(String toMail, String subject, String text) {
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
