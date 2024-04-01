package freshtrash.freshtrashbackend.utils;

import freshtrash.freshtrashbackend.exception.FileException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import org.springframework.util.StringUtils;

import java.util.UUID;

public class FileUtils {
    private static final String EXTENSION_SEPARATOR = ".";

    /**
     * 확장자 index 반환
     */
    public static int indexOfExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            throw new FileException(ErrorCode.INVALID_FIlE_NAME);
        }
        return filename.lastIndexOf(EXTENSION_SEPARATOR);
    }

    /**
     * 확장자만 추출 (e.g. png)
     */
    public static String getExtension(String filename) {
        int index = indexOfExtension(filename);
        return filename.substring(index + 1);
    }

    /**
     * 임의의 고유한 파일명 생성
     */
    public static String generateUniqueFileName(String filename) {
        return UUID.randomUUID() + EXTENSION_SEPARATOR + getExtension(filename);
    }
}
