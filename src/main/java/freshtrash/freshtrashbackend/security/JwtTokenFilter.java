package freshtrash.freshtrashbackend.security;

import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.exception.AuthException;
import freshtrash.freshtrashbackend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    public static final String ACCESS_TOKEN_HEADER = HttpHeaders.AUTHORIZATION;
    public static final String TOKEN_PREFIX = "Bearer";
    private final TokenProvider tokenProvider;
    private final MemberService memberService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = parseBearerToken(request);
        try {
            if (!StringUtils.hasText(accessToken)) {
                filterChain.doFilter(request, response);
                return;
            }
            setAuthentication(request, accessToken);
        } catch (Exception e) {
            log.error("Error occurs during authenticate, {}", e.getMessage());
            throw new AuthException("Failed authenticate");
        }
        filterChain.doFilter(request, response);
    }

    /**
     * reqeust header의 토큰 파싱
     */
    private String parseBearerToken(HttpServletRequest request) {
        String token = request.getHeader(ACCESS_TOKEN_HEADER);
        if (token != null && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length()).trim();
        }
        return token;
    }

    private void setAuthentication(HttpServletRequest request, String accessToken) {
        MemberPrincipal memberPrincipal = getMemberPrincipal(accessToken);
        UsernamePasswordAuthenticationToken authenticated = UsernamePasswordAuthenticationToken.authenticated(
                memberPrincipal, accessToken, memberPrincipal.getAuthorities());
        authenticated.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticated);
    }

    private MemberPrincipal getMemberPrincipal(String accessToken) {
        Long memberId = tokenProvider.getMemberIdFromToken(accessToken);
        return memberService.getMemberCache(memberId);
    }
}
