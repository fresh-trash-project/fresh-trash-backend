package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.cache.EmailCodeCache;
import freshtrash.freshtrashbackend.exception.MailException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.EmailCodeCacheRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import javax.mail.internet.MimeMessage;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class MailServiceTest {
    @InjectMocks
    private MailService mailService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private EmailCodeCacheRepository emailCodeCacheRepository;

    @Mock
    private MimeMessage mimeMessage;

    @Test
    @DisplayName("받는 이메일, 제목, 인증 코드를 입력받아 전송한다.")
    void given_emailAndSubjectAndCode_when_saveCodeCache_then_sendMail() {
        // given
        String email = "testUser@gmail.com", subject = "인증 코드", code = "12345";
        EmailCodeCache emailCodeCache = EmailCodeCache.of(email, code);
        given(emailCodeCacheRepository.save(emailCodeCache)).willReturn(emailCodeCache);
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);
        willDoNothing().given(mailSender).send(mimeMessage);
        // when
        mailService.sendMailWithCode(email, subject, code);
        // then
    }

    @Test
    @DisplayName("email을 입력받아 캐싱한 인증 코드를 조회한다.")
    void given_email_when_then_findEmailCodeCache() {
        // given
        String email = "testUser@gmail.com", code = "12345";
        EmailCodeCache emailCodeCache = new EmailCodeCache(email, code);
        given(emailCodeCacheRepository.findById(email)).willReturn(Optional.of(emailCodeCache));
        // when
        EmailCodeCache savedEmailCodeCache = mailService.getEmailCodeCache(email);
        // then
        assertThat(savedEmailCodeCache.email()).isEqualTo(email);
        assertThat(savedEmailCodeCache.code()).isEqualTo(code);
    }

    @Test
    @DisplayName("email과 인증 코드를 입력받아 전송한 인증 코드와 일치한다면 아무것도 반환하지 않는다.")
    void given_emailAndCode_when_equalsCode_then_notAnyReturn() {
        // given
        String email = "testUser@gmail.com", code = "12345";
        EmailCodeCache emailCodeCache = new EmailCodeCache(email, code);
        given(emailCodeCacheRepository.findById(email)).willReturn(Optional.of(emailCodeCache));
        // when
        assertThatCode(() -> mailService.verifyEmailCode(email, code)).doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("email과 인증 코드를 입력받고 입력받은 인증 코드가 빈 문자열이라면 예외가 발생한다.")
    void given_emailAndCode_when_codeIsBlank_then_throwException() {
        // given
        String email = "testUser@gmail.com", code = "";
        EmailCodeCache emailCodeCache = new EmailCodeCache(email, code);
        given(emailCodeCacheRepository.findById(email)).willReturn(Optional.of(emailCodeCache));
        // when & then
        assertThatThrownBy(() -> mailService.verifyEmailCode(email, code))
                .isInstanceOf(MailException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMPTY_AUTH_CODE);
    }

    @Test
    @DisplayName("email과 인증 코드를 입력받고 입력받은 인증 코드가 전송한 코드와 일치하지 않는다면 예외가 발생한다.")
    void given_emailAndCode_when_notEqualscode_then_throwException() {
        // given
        String email = "testUser@gmail.com", code = "54321";
        EmailCodeCache emailCodeCache = new EmailCodeCache(email, "12345");
        given(emailCodeCacheRepository.findById(email)).willReturn(Optional.of(emailCodeCache));
        // when & then
        assertThatThrownBy(() -> mailService.verifyEmailCode(email, code))
                .isInstanceOf(MailException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNMATCHED_AUTH_CODE);
    }

    @Test
    @DisplayName("email과 임시비밀번호를 입력받아 메일로 전송한다.")
    void given_emailAndPassword_when_then_sendMail() {
        // given
        String email = "testUser@gmail.com", temporaryPassword = "tmppw";
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);
        willDoNothing().given(mailSender).send(mimeMessage);
        // when
        assertThatCode(() -> mailService.sendMailWithTemporaryPassword(email, temporaryPassword))
                .doesNotThrowAnyException();
        // then
    }
}