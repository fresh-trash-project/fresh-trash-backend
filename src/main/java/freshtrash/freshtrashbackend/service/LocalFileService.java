package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.properties.LocalFileProperties;
import freshtrash.freshtrashbackend.exception.FileException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class LocalFileService implements FileService {
    private final LocalFileProperties localFileProperties;

    @Override
    public void uploadFile(MultipartFile file, String fileName) {
        try (FileOutputStream fos = new FileOutputStream(createFileInstance(fileName))) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            throw new FileException(ErrorCode.FILE_CANT_SAVE, e);
        }
    }

    @Override
    public void deleteFileIfExists(String fileName) {
        File file = createFileInstance(fileName);
        // 파일이 존재하지 않을 경우 예외 처리
        if (file.exists()) {
            // 파일을 정상적으로 삭제할 경우 true를 반환하며 false를 반환할 경우 예외 처리
            if (!file.delete()) {
                throw new FileException(ErrorCode.FILE_CANT_DELETE);
            }
        } else {
            throw new FileException(ErrorCode.FILE_NOT_FOUND);
        }
    }

    @Override
    public void deleteOrNotOldFile(String oldFileName, String newFileName) {
        if (StringUtils.hasText(oldFileName) && !oldFileName.equals(newFileName)) deleteFileIfExists(oldFileName);
    }

    private File createFileInstance(String fileName) {
        try {
            return new File(localFileProperties.absolutePath(), fileName);
        } catch (NullPointerException e) {
            throw new FileException(ErrorCode.FILE_NOT_FOUND, e);
        }
    }
}
