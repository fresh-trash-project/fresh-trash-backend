package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.request.LoginRequest;
import freshtrash.freshtrashbackend.dto.request.SignUpRequest;
import freshtrash.freshtrashbackend.dto.response.ApiResponse;
import freshtrash.freshtrashbackend.dto.response.LoginResponse;
import freshtrash.freshtrashbackend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthApi {
    private final MemberService memberService;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody @Valid SignUpRequest signUpRequest) {
        memberService.registerMember(signUpRequest.toEntity());
        return ResponseEntity.ok(ApiResponse.of("you're successfully sign up. you can be login."));
    }

    /**
     * 로그인
     */
    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> signIn(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse loginResponse = memberService.signIn(loginRequest.email(), loginRequest.password());
        return ResponseEntity.ok(loginResponse);
    }
}
