package freshtrash.freshtrashbackend.config;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.domain.member.entity.Member;
import freshtrash.freshtrashbackend.domain.member.entity.constants.AccountStatus;
import freshtrash.freshtrashbackend.domain.member.entity.constants.LoginType;
import freshtrash.freshtrashbackend.domain.member.service.MemberService;
import freshtrash.freshtrashbackend.domain.member.service.TokenProvider;
import freshtrash.freshtrashbackend.global.config.security.CustomOAuth2SuccessHandler;
import freshtrash.freshtrashbackend.global.config.security.Http401UnauthorizedAuthenticationEntryPoint;
import freshtrash.freshtrashbackend.global.config.security.SecurityConfig;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@Import(SecurityConfig.class)
public class TestSecurityConfig {
    @MockBean
    private MemberService memberService;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private Http401UnauthorizedAuthenticationEntryPoint http401UnauthorizedAuthenticationEntryPoint;

    @MockBean
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @BeforeTestMethod
    void securitySetUp() {
        String userEmail = "testUser@gmail.com";
        given(memberService.getMemberByEmail(eq(userEmail))).willReturn(createMember(123L, userEmail));
    }

    private Member createMember(Long id, String email) {
        return Fixture.createMember(id, email, "pw", email.split("@")[0], LoginType.EMAIL, AccountStatus.ACTIVE);
    }
}
