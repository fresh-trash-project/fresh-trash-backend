package freshtrash.freshtrashbackend.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 로컬(개발) 환경과 운영 환경을 구분하여 구현합니다.
 */
public interface FileService {
    void uploadFile(MultipartFile file, String fileName);

    void deleteFileIfExists(String fileName);

    /**
     * oldFileName -> newFileName 으로 수정되었을 경우 파일을 삭제
     */
    void deleteOrNotOldFile(String oldFileName, String newFileName);
}
