package freshtrash.freshtrashbackend.security;

import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private static final String REDIRECT_URI = "http://localhost:5173/";
    private static final String TOKEN_COOKIE_NAME = "accessToken";
    private static final String OAUTH_INFO_COOKIE_NAME = "oauthInfo";
    private static final int COOKIE_MAX_AGE = 10 * 60; // 10분

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        MemberPrincipal principal = (MemberPrincipal) authentication.getPrincipal();
        // 회원가입되지 않았을 경우 회원가입 페이지로 redirect
        if (Objects.isNull(principal.id())) {
            addCookie(
                    response, OAUTH_INFO_COOKIE_NAME, String.format("%s_%s", principal.email(), principal.nickname()));
            response.sendRedirect(REDIRECT_URI + "SignUpSignIn");
        } else {
            String accessToken = tokenProvider.generateAccessToken(principal.id());
            addCookie(response, TOKEN_COOKIE_NAME, accessToken);
            response.sendRedirect(REDIRECT_URI);
        }
    }

    private void addCookie(HttpServletResponse response, String cookieName, String cookieValue) {
        Cookie cookie = new Cookie(cookieName, URLEncoder.encode(cookieValue, StandardCharsets.UTF_8));
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
