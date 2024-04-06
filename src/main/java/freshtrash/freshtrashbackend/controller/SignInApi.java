package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.request.LoginRequest;
import freshtrash.freshtrashbackend.dto.response.LoginResponse;
import freshtrash.freshtrashbackend.service.SigInService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/signin")
@RequiredArgsConstructor
public class SignInApi {
    private final SigInService signInService;

    @PostMapping
    public ResponseEntity<LoginResponse> signIn(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse loginResponse = signInService.signIn(loginRequest.email(), loginRequest.password());
        return ResponseEntity.ok(loginResponse);
    }
}
