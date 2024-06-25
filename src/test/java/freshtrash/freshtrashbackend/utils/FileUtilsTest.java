package freshtrash.freshtrashbackend.utils;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.exception.FileException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileUtilsTest {

    @Test
    @DisplayName("Multipartfile 객체를 인자로 받아 임의의 고유한 파일명을 생성하여 반환한다.")
    void given_mutipartfile_when_then_uniqueFileName() {
        // given
        MockMultipartFile file = Fixture.createMultipartFileOfImage("image");
        // when & then
        String fileName = FileUtils.generateUniqueFileName(file);
        assertThat(fileName).isNotNull();
    }

    @Test
    @DisplayName("Multipartfile 객체의 파일명이 빈 문자열일 경우 예외가 발생한다.")
    void given_multipartfile_when_fileNameIsBlank_then_throwException() {
        // given
        MockMultipartFile file =
                new MockMultipartFile("modelFile", "", "text/plain", "content".getBytes(StandardCharsets.UTF_8));
        // when & then
        assertThatThrownBy(() -> FileUtils.generateUniqueFileName(file))
                .isInstanceOf(FileException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_FIlE_NAME);
    }
}