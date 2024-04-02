package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.request.EmailRequest;
import freshtrash.freshtrashbackend.dto.response.EmailResponse;
import freshtrash.freshtrashbackend.service.MailServiceInterface;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/mail")
@RequiredArgsConstructor
public class MailApi {
    private final MailServiceInterface mailService;
    @PostMapping("send-code")
    public ResponseEntity<EmailResponse> sendMailWithCode(@RequestBody @Valid EmailRequest request) {
        // TO-DO 중복된 이메일 체크
        String code = UUID.randomUUID().toString().substring(0, 8);
        String subject = "fresh-trash 메일 인증";
        String text = "fresh-trash 메일 인증 코드입니다. <br/>인증코드:" + code;
        mailService.sendMailWithCode(request.email(), subject, text);
        return ResponseEntity.ok(new EmailResponse(true, request.email()));
    }
}
