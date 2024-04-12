package freshtrash.freshtrashbackend.dto.request;

import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.entity.constants.AccountStatus;
import freshtrash.freshtrashbackend.entity.constants.LoginType;
import freshtrash.freshtrashbackend.entity.constants.UserRole;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record SignUpRequest(@NotBlank String nickname, @NotBlank @Email String email, @NotBlank String password) {
    public Member toEntity() {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .loginType(LoginType.EMAIL)
                .userRole(UserRole.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }
}
