package freshtrash.freshtrashbackend.config;

import freshtrash.freshtrashbackend.security.SecurityConfig;
import freshtrash.freshtrashbackend.service.MemberService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@Import(SecurityConfig.class)
public class TestSecurityConfig {
    @MockBean private MemberService memberService;
}
