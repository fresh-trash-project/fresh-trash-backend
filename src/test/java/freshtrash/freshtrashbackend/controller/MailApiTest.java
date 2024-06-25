package freshtrash.freshtrashbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.dto.request.EmailRequest;
import freshtrash.freshtrashbackend.service.MailService;
import freshtrash.freshtrashbackend.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(MailApi.class)
@Import(TestSecurityConfig.class)
class MailApiTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MailService mailService;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("인증 코드 메일 발송")
    void given_emailRequest_when_notDuplicateEmail_then_sendMailCode() throws Exception {
        // given
        EmailRequest emailRequest = new EmailRequest("testUser@gmail.com", null);
        willDoNothing().given(memberService).checkEmailDuplication(emailRequest.email());
        willDoNothing().given(mailService).sendMailWithCode(eq(emailRequest.email()), anyString(), anyString());
        // when
        mvc.perform(post("/api/v1/mail/send-code")
                        .content(objectMapper.writeValueAsString(emailRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists());
        // then
    }

    @Test
    @DisplayName("이메일 인증코드 확인")
    void given_emailRequest_when_then_verifyEmailCode() throws Exception {
        // given
        EmailRequest emailRequest = new EmailRequest("testUser@gmail.com", "12345");
        willDoNothing().given(mailService).verifyEmailCode(emailRequest.email(), emailRequest.code());
        // when
        mvc.perform(post("/api/v1/mail/verify")
                        .content(objectMapper.writeValueAsString(emailRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        // then
    }

    @Test
    @DisplayName("임시 비밀번호 전송")
    void given_emailRequest_when_then_sendTempPassword() throws Exception {
        // given
        EmailRequest emailRequest = new EmailRequest("testUser@gmail.com", null);
        willDoNothing().given(memberService).updatePassword(eq(emailRequest.email()), anyString());
        willDoNothing().given(mailService).sendMailWithTemporaryPassword(eq(emailRequest.email()), anyString());
        // when
        mvc.perform(post("/api/v1/mail/find-pass")
                        .content(objectMapper.writeValueAsString(emailRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        // then
    }
}