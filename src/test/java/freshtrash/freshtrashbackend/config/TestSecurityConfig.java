package freshtrash.freshtrashbackend.config;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.entity.constants.AccountStatus;
import freshtrash.freshtrashbackend.entity.constants.LoginType;
import freshtrash.freshtrashbackend.security.CustomOAuth2SuccessHandler;
import freshtrash.freshtrashbackend.security.Http401UnauthorizedAuthenticationEntryPoint;
import freshtrash.freshtrashbackend.security.SecurityConfig;
import freshtrash.freshtrashbackend.security.TokenProvider;
import freshtrash.freshtrashbackend.service.MemberService;
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
