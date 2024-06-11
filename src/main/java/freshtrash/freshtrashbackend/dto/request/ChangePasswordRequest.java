package freshtrash.freshtrashbackend.dto.request;

import javax.validation.constraints.Pattern;

public record ChangePasswordRequest(
        @Pattern(
                        regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{8,20}",
                        message = "비밀번호는 영문자와 숫자, 특수기호가 적어도 1개 이상 포함된 8자~20자의 비밀번호여야 합니다.")
                String oldPassword,
        @Pattern(
                        regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{8,20}",
                        message = "비밀번호는 영문자와 숫자, 특수기호가 적어도 1개 이상 포함된 8자~20자의 비밀번호여야 합니다.")
                String newPassword) {}
