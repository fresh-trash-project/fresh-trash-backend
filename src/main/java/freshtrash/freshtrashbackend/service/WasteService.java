package freshtrash.freshtrashbackend.service;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.dto.response.WasteResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.exception.FileException;
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

    public Waste getWaste(Long wasteId) {
        return wasteRepository.findById(wasteId).orElseThrow(() -> new WasteException(ErrorCode.NOT_FOUND_WASTE));
    }

    public Page<WasteResponse> getWastes(String district, Predicate predicate, Pageable pageable) {
        return wasteRepository.findAll(district, predicate, pageable).map(WasteResponse::fromEntity);
    }

    @Transactional
    public WasteResponse addWaste(MultipartFile imgFile, WasteRequest wasteRequest, MemberPrincipal memberPrincipal) {
        // 주소가 입력되지 않았을 경우
        if (Objects.isNull(wasteRequest.address())) throw new WasteException(ErrorCode.EMPTY_ADDRESS);
        String savedFileName = FileUtils.generateUniqueFileName(imgFile);
        Waste waste = Waste.fromRequest(wasteRequest, savedFileName, memberPrincipal.id());

        Waste savedWaste = wasteRepository.save(waste);
        // 이미지 파일 저장
        fileService.uploadFile(imgFile, savedFileName);
        return WasteResponse.fromEntity(savedWaste, memberPrincipal);
    }

    @Transactional
    public WasteResponse updateWaste(
            Long wasteId, MultipartFile imgFile, WasteRequest wasteRequest, MemberPrincipal memberPrincipal) {

        if (!FileUtils.isValid(imgFile)) {
            throw new FileException(ErrorCode.INVALID_FIlE);
        }

        // DB 업데이트
        String updatedFileName = FileUtils.generateUniqueFileName(imgFile);
        Waste updatedWaste = Waste.fromRequest(wasteRequest, updatedFileName, memberPrincipal.id());
        updatedWaste.setId(wasteId);
        wasteRepository.save(updatedWaste);
        // 수정된 파일 저장
        fileService.uploadFile(imgFile, updatedFileName);

        return WasteResponse.fromEntity(updatedWaste, memberPrincipal);
    }

    public void deleteWaste(Long wasteId) {
        wasteRepository.deleteById(wasteId);
    }

    public FileNameSummary findFileNameOfWaste(Long wasteId) {
        return wasteRepository
                .findFileNameById(wasteId)
                .orElseThrow(() -> new WasteException(ErrorCode.NOT_FOUND_WASTE));
    }

    /**
     * 작성자인지 확인
     */
    public boolean isWriterOfArticle(Long wasteId, Long memberId) {
        return wasteRepository.existsByIdAndMember_Id(wasteId, memberId);
    }

    public void updateViewCount(Long wasteId) {
        wasteRepository.updateViewCount(wasteId);
    }
}