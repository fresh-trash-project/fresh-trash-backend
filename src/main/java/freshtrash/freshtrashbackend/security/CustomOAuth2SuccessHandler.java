package freshtrash.freshtrashbackend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import freshtrash.freshtrashbackend.dto.response.LoginResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private final ObjectMapper mapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        MemberPrincipal principal = (MemberPrincipal) authentication.getPrincipal();
        String accessToken = tokenProvider.generateAccessToken(principal.id());
        response.getOutputStream().println(mapper.writeValueAsString(LoginResponse.of(accessToken)));
        // TODO: 회원가입되지 않은 계정일 경우 처리
    }
}
