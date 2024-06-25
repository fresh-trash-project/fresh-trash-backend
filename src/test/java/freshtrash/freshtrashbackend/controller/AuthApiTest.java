package freshtrash.freshtrashbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.dto.request.LoginRequest;
import freshtrash.freshtrashbackend.dto.request.SignUpRequest;
import freshtrash.freshtrashbackend.dto.response.LoginResponse;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(AuthApi.class)
@Import(TestSecurityConfig.class)
class AuthApiTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 요청")
    void given_signUpRequest_when_then_registerMember() throws Exception {
        // given
        Member member = Fixture.createMember();
        SignUpRequest signUpRequest = FixtureDto.createSignUpRequest();
        given(memberService.registerMember(member)).willReturn(member);
        // when
        mvc.perform(post("/api/v1/auth/signup")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        // then
    }

    @Test
    @DisplayName("로그인 요청")
    void given_loginRequest_when_then_signInMember() throws Exception {
        // given
        LoginRequest loginRequest = FixtureDto.createLoginRequest();
        LoginResponse loginResponse = new LoginResponse("accessToken");
        given(memberService.signIn(loginRequest.email(), loginRequest.password()))
                .willReturn(loginResponse);
        // when
        mvc.perform(post("/api/v1/auth/signin")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(loginResponse.accessToken()));
        // then
    }

    @Test
    @DisplayName("닉네임 중복확인")
    void given_nickname_when_then_checkDuplication() throws Exception {
        // given
        String nickname = "testUser";
        willDoNothing().given(memberService).checkNicknameDuplication(nickname);
        // when
        mvc.perform(get("/api/v1/auth/check-nickname").queryParam("nickname", nickname))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("사용가능한 닉네임입니다."));
        // then
    }
}