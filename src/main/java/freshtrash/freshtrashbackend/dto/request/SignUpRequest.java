package freshtrash.freshtrashbackend.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record SignUpRequest(@NotBlank String nickname, @NotBlank @Email String email, @NotBlank String password) {}
