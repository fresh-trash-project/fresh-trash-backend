package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.request.SignUpRequest;
import freshtrash.freshtrashbackend.dto.response.ApiResponse;
import freshtrash.freshtrashbackend.service.impl.AuthService;
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
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignUpRequest signUpRequest) {
        authService.registerMember(signUpRequest.toEntity());
        return ResponseEntity.ok(ApiResponse.of("you're successfully sign up. you can be login."));
    }
}
