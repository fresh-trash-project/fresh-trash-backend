package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.request.EmailRequest;
import freshtrash.freshtrashbackend.dto.response.EmailResponse;
import freshtrash.freshtrashbackend.dto.response.MailApiResponse;
import freshtrash.freshtrashbackend.service.MailServiceInterface;
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
@RequestMapping("/api/v1/mail")
@RequiredArgsConstructor
public class MailApi {
    private final MailServiceInterface mailService;

    @PostMapping("/send-code")
    public ResponseEntity<EmailResponse> sendMailWithCode(@RequestBody @Valid EmailRequest request) {
        // TODO 중복된 이메일 체크
        String code = UUID.randomUUID().toString().substring(0, 8);
        String subject = "fresh-trash 메일 인증";
        mailService.sendMailWithCode(request.email(), subject, code);

        return ResponseEntity.ok(EmailResponse.of(code));
    }

    @PostMapping("/verify")
    public ResponseEntity<MailApiResponse> verifyEmail(@RequestBody @Valid EmailRequest request) {
        boolean result = mailService.verifyEmailCode(request.email(), request.code());

        return ResponseEntity.ok(MailApiResponse.of("isVerifiedEmail: " + result));
    }
}
