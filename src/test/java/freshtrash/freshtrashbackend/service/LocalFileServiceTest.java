package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.dto.properties.LocalFileProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatCode;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class LocalFileServiceTest {
    @InjectMocks
    private LocalFileService localFileService;

    @Mock
    private LocalFileProperties localFileProperties;

    @Test
    @DisplayName("파일 업로드")
    void given_fileAndFileName_when_then_updateFile() {
        // given
        MockMultipartFile image = Fixture.createMultipartFileOfImage("image");
        // when
        assertThatCode(() -> localFileService.uploadFile(image, "test.png")).doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("파일이 존재한다면 삭제하고 존재하지 않는다면 로그를 출력한다.")
    void given_fileName_when_existsFile_then_delete() {
        // given
        String fileName = "test.png";
        // when
        assertThatCode(() -> localFileService.deleteFileIfExists(fileName)).doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("이전 파일명과 새 파일명을 비교하여 달라졌을 경우 파일을 삭제한다.")
    void given_oldFileNameAndNewFileName_when_differentName_then_delete() {
        // given
        String oldFileName = "test.png", newFileName = "new.png";
        // when
        assertThatCode(() -> localFileService.deleteOrNotOldFile(oldFileName, newFileName))
                .doesNotThrowAnyException();
        ;
        // then
    }
}