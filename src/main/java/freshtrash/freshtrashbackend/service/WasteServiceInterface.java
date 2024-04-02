package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.WasteDto;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import org.springframework.web.multipart.MultipartFile;

public interface WasteServiceInterface {
    WasteDto addWaste(MultipartFile modelFile, WasteRequest wasteRequest);

    void deleteWaste(Long wasteId);
}
