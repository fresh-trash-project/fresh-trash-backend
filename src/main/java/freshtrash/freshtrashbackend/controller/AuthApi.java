package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.request.LoginRequest;
import freshtrash.freshtrashbackend.dto.request.SignUpRequest;
import freshtrash.freshtrashbackend.dto.response.ApiResponse;
import freshtrash.freshtrashbackend.dto.response.LoginResponse;
import freshtrash.freshtrashbackend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("you're successfully sign up. you can be login."));
    }

    /**
     * 로그인
     */
    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> signIn(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse loginResponse = memberService.signIn(loginRequest.email(), loginRequest.password());
        return ResponseEntity.ok(loginResponse);
    }

    /**
     * 닉네임 중복확인
     */
    @GetMapping("/check-nickname")
    public ResponseEntity<ApiResponse<String>> checkNickname(@RequestParam String nickname) {
        memberService.checkNicknameDuplication(nickname);
        return ResponseEntity.ok(ApiResponse.of("사용가능한 닉네임입니다."));
    }
}
