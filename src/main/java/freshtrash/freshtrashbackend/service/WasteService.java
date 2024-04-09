package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.WasteDto;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.exception.WasteException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.WasteRepository;
import freshtrash.freshtrashbackend.repository.projections.FileNameSummary;
import freshtrash.freshtrashbackend.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class WasteService {
    private final WasteRepository wasteRepository;
    private final FileService fileService;

    @Transactional(readOnly = true)
    public Waste getWasteEntity(Long wasteId) {
        return wasteRepository.findById(wasteId).orElseThrow(() -> new WasteException(ErrorCode.NOT_FOUND_WASTE));
    }

    @Transactional(readOnly = true)
    public Page<WasteDto> getWastes(Pageable pageable) {
        return wasteRepository.findAll(pageable).map(WasteDto::fromEntity);
    }

    public WasteDto addWaste(MultipartFile imgFile, WasteRequest wasteRequest, MemberPrincipal memberPrincipal) {
        // 주소가 입력되지 않았을 경우
        if (Objects.isNull(wasteRequest.address())) throw new WasteException(ErrorCode.EMPTY_ADDRESS);
        String savedFileName = FileUtils.generateUniqueFileName(imgFile);
        Waste waste = wasteRequest.toEntity(savedFileName, memberPrincipal.id());

        Waste savedWaste = wasteRepository.save(waste);
        // 이미지 파일 저장
        fileService.uploadFile(imgFile, savedFileName);
        return WasteDto.fromEntity(savedWaste, memberPrincipal);
    }

    public WasteDto updateWaste(
            MultipartFile imgFile, WasteRequest wasteRequest, String savedFileName, MemberPrincipal memberPrincipal) {
        String updatedFileName = FileUtils.generateUniqueFileName(imgFile);
        Waste updatedWaste = wasteRequest.toEntity(updatedFileName, memberPrincipal.id());

        // 파일은 유효할 경우에만 수정합니다
        if (FileUtils.isValid(imgFile)) {
            // DB 업데이트
            wasteRepository.save(updatedWaste);
            // 수정된 파일 저장
            fileService.uploadFile(imgFile, updatedFileName);
            wasteRepository.flush();
            // 저장된 파일 삭제
            fileService.deleteFileIfExists(savedFileName);
        }

        return WasteDto.fromEntity(updatedWaste, memberPrincipal);
    }

    public void deleteWaste(Long wasteId, String savedFileName) {
        wasteRepository.deleteById(wasteId);
        // 파일 삭제
        fileService.deleteFileIfExists(savedFileName);
    }

    @Transactional(readOnly = true)
    public String findFileNameOfWaste(Long wasteId) {
        return wasteRepository
                .findFileNameById(wasteId)
                .map(FileNameSummary::fileName)
                .orElseThrow(() -> new WasteException(ErrorCode.NOT_FOUND_WASTE));
    }

    /**
     * 작성자인지 확인
     */
    public boolean isWriterOfArticle(Long wasteId, Long memberId) {
        return wasteRepository.existsByIdAndMember_Id(wasteId, memberId);
    }
}
