package freshtrash.freshtrashbackend.security;

import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.entity.constants.AccountStatus;
import freshtrash.freshtrashbackend.entity.constants.LoginType;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import freshtrash.freshtrashbackend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private final MemberService memberService;
    private static final String TOKEN_COOKIE_NAME = "accessToken";
    private static final int COOKIE_MAX_AGE = 10 * 60; // 10분

    @Value("${oauth2.redirect-uri}")
    private String REDIRECT_URI;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        MemberPrincipal principal = (MemberPrincipal) authentication.getPrincipal();
        // 회원가입되지 않았을 경우 회원가입 처리
        Long memberId = Optional.ofNullable(principal.id()).orElseGet(() -> memberService
                .registerMember(Member.builder()
                        .email(principal.email())
                        .password(UUID.randomUUID().toString())
                        .nickname(principal.nickname())
                        .loginType(LoginType.OAUTH)
                        .userRole(UserRole.USER)
                        .accountStatus(AccountStatus.ACTIVE)
                        .build())
                .getId());
        String accessToken = tokenProvider.generateAccessToken(memberId);
        addCookie(response, accessToken);
        response.sendRedirect(REDIRECT_URI);
    }

    private void addCookie(HttpServletResponse response, String cookieValue) {
        Cookie cookie = new Cookie(TOKEN_COOKIE_NAME, URLEncoder.encode(cookieValue, StandardCharsets.UTF_8));
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
