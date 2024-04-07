package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.response.LoginResponse;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.exception.AuthException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SigInService {
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;

    /**
     * 로그인
     */
    public LoginResponse signIn(String email, String password) {
        Member member = memberService.getMemberEntityByEmail(email);
        checkPassword(password, member.getPassword());
        // 토큰 발급
        String accessToken = generateAccessToken(email, member);
        return LoginResponse.of(accessToken);
    }

    /**
     * 비밀번호 일치 확인
     * @param inputPassword 입력한 비밀번호
     * @param existPassword 기존 비밀번호
     */
    private void checkPassword(String inputPassword, String existPassword) {
        if (!passwordEncoder.matches(inputPassword, existPassword)) {
            throw new AuthException("not matched password");
        }
    }

    /**
     * AccessToken 발급
     */
    private String generateAccessToken(String email, Member member) {
        return tokenProvider.generateAccessToken(
                email,
                member.getNickname(),
                String.format("%s:%s", member.getId(), member.getUserRole().getName()),
                member.getRating(),
                member.getFileName(),
                member.getAddress());
    }
}
