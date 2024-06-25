package freshtrash.freshtrashbackend.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordGeneratorTest {

    @Test
    @DisplayName("영어 소문자, 숫자, 특수문자를 적어도 1개 이상 포함한 8 ~ 20자의 임의 비밀번호를 생성하여 반환한다.")
    void should_generateTemporaryPassword() {
        // given
        // when
        String password = PasswordGenerator.generateTemporaryPassword();
        // then
        assertThat(password).isNotBlank();
    }
}