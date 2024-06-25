package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.request.EmailRequest;
import freshtrash.freshtrashbackend.dto.response.EmailResponse;
import freshtrash.freshtrashbackend.service.MailService;
import freshtrash.freshtrashbackend.service.MemberService;
import freshtrash.freshtrashbackend.utils.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mail")
public class MailApi {
    private final MailService mailService;
    private final MemberService memberService;

    /**
     * 인증코드 메일 발송
     */
    @PostMapping("/send-code")
    public ResponseEntity<EmailResponse> sendMailWithCode(@RequestBody @Valid EmailRequest request) {
        memberService.checkEmailDuplication(request.email());
        // TODO: 이메일 Validation API를 사용 (MailValidationUtils) (사용 제한이 있음)

        String code = UUID.randomUUID().toString().substring(0, 8);
        String subject = "fresh-trash 메일 인증";
        mailService.sendMailWithCode(request.email(), subject, code);

        return ResponseEntity.ok(EmailResponse.of(code));
    }

    /**
     * 이메일 인증코드 확인
     */
    @PostMapping("/verify")
    public ResponseEntity<Void> verifyEmail(@RequestBody @Valid EmailRequest request) {
        mailService.verifyEmailCode(request.email(), request.code());

        return ResponseEntity.ok(null);
    }

    /**
     * 임시 비밀번호 전송
     */
    @PostMapping("/find-pass")
    public ResponseEntity<Void> sendTemporaryPassword(@RequestBody @Valid EmailRequest request) {
        String temporaryPassword = PasswordGenerator.generateTemporaryPassword();
        memberService.updatePassword(request.email(), temporaryPassword);
        mailService.sendMailWithTemporaryPassword(request.email(), temporaryPassword);
        return ResponseEntity.ok(null);
    }
}
