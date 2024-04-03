package freshtrash.freshtrashbackend.service.impl;

import freshtrash.freshtrashbackend.dto.WasteDto;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.exception.WasteException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.WasteRepository;
import freshtrash.freshtrashbackend.service.FileServiceInterface;
import freshtrash.freshtrashbackend.service.WasteServiceInterface;
import freshtrash.freshtrashbackend.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class WasteService implements WasteServiceInterface {
    private final WasteRepository wasteRepository;
    private final FileServiceInterface fileService;

    @Override
    public WasteDto addWaste(MultipartFile imgFile, WasteRequest wasteRequest) {
        // TODO: 유저 정보 추가
        // 주소가 입력되지 않았을 경우
        if (Objects.isNull(wasteRequest.address())) throw new WasteException(ErrorCode.EMPTY_ADDRESS);

        String savedFileName = FileUtils.generateUniqueFileName(imgFile.getOriginalFilename());
        Waste waste = wasteRequest.toEntity(savedFileName);

        Waste savedWaste = wasteRepository.save(waste);
        // 이미지 파일 저장
        fileService.uploadFile(imgFile, savedFileName);
        return WasteDto.fromEntity(savedWaste);
    }
}
