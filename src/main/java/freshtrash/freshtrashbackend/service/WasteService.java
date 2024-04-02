package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.WasteDto;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.repository.WasteRepository;
import freshtrash.freshtrashbackend.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class WasteService implements WasteServiceInterface {
    private final WasteRepository wasteRepository;

    @Override
    public WasteDto addWaste(MultipartFile modelFile, WasteRequest wasteRequest) {
        // TODO: 유저 정보 추가
        // TODO: 파일 저장 로직 추가
        Waste waste = wasteRepository.save(
                wasteRequest.toEntity(FileUtils.generateUniqueFileName(modelFile.getOriginalFilename())));
        return WasteDto.fromEntity(waste);
    }
}
