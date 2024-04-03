package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.WasteDto;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.entity.Waste;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface WasteServiceInterface {
    Waste getWasteEntity(Long wasteId);

    WasteDto getWasteDto(Long wasteId);

    Page<WasteDto> getWastes(Pageable pageable);

    WasteDto addWaste(MultipartFile modelFile, WasteRequest wasteRequest);

    void deleteWaste(Long wasteId);

    String findFileNameOfWaste(Long wasteId);
}
