package freshtrash.freshtrashbackend.service.impl;

import freshtrash.freshtrashbackend.dto.WasteDto;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.repository.WasteRepository;
import freshtrash.freshtrashbackend.service.FileServiceInterface;
import freshtrash.freshtrashbackend.service.WasteServiceInterface;
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
    private final FileServiceInterface fileService;

    @Override
    public WasteDto addWaste(MultipartFile imgFile, WasteRequest wasteRequest) {
        // TODO: 유저 정보 추가
        String savedFileName = FileUtils.generateUniqueFileName(imgFile.getOriginalFilename());
        Waste waste = wasteRepository.save(wasteRequest.toEntity(savedFileName));
        // 이미지 파일 저장
        fileService.uploadFile(imgFile, savedFileName);
        return WasteDto.fromEntity(waste);
    }
}
