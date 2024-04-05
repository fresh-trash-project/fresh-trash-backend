package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.WasteDto;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.exception.WasteException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.WasteRepository;
import freshtrash.freshtrashbackend.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class WasteService {
    private final WasteRepository wasteRepository;
    private final FileService fileService;

    @Transactional(readOnly = true)
    public Waste getWasteEntity(Long wasteId) {
        return wasteRepository.findById(wasteId).orElseThrow(() -> new WasteException(ErrorCode.NOT_FOUND_WASTE));
    }

    @Transactional(readOnly = true)
    public WasteDto getWasteDto(Long wasteId) {
        return WasteDto.fromEntity(getWasteEntity(wasteId));
    }

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

    public WasteDto updateWaste(MultipartFile imgFile, WasteRequest wasteRequest, Long wasteId, MemberPrincipal memberPrincipal) {
        // TODO: 수정 로직 변경 -> wasteRequest.toEntity() 후 save
        Waste savedWaste = getWasteEntity(wasteId);

        // "제목, 본문, 가격, 카테고리, 상품 상태, 판매 상태, 주소"를 수정할 수 있습니다
        if (isUpdatedArticleData(savedWaste.getTitle(), wasteRequest.title())) {
            savedWaste.setTitle(wasteRequest.title());
        }
        if (isUpdatedArticleData(savedWaste.getContent(), wasteRequest.content())) {
            savedWaste.setContent(wasteRequest.content());
        }
        if (isUpdatedArticleData(savedWaste.getWastePrice(), wasteRequest.wastePrice())) {
            savedWaste.setWastePrice(wasteRequest.wastePrice());
        }
        if (isUpdatedArticleData(savedWaste.getWasteCategory(), wasteRequest.wasteCategory())) {
            savedWaste.setWasteCategory(wasteRequest.wasteCategory());
        }
        if (isUpdatedArticleData(savedWaste.getWasteStatus(), wasteRequest.wasteStatus())) {
            savedWaste.setWasteStatus(wasteRequest.wasteStatus());
        }
        if (isUpdatedArticleData(savedWaste.getSellStatus(), wasteRequest.sellStatus())) {
            savedWaste.setSellStatus(wasteRequest.sellStatus());
        }
        if (isUpdatedArticleData(savedWaste.getAddress(), wasteRequest.address())) {
            savedWaste.setAddress(wasteRequest.address());
        }

        // 파일은 유효할 경우에만 수정합니다
        if (FileUtils.isValid(imgFile)) {
            String savedFileName = savedWaste.getFileName();
            String updatedFileName = FileUtils.generateUniqueFileName(imgFile);
            // DB 업데이트
            savedWaste.setFileName(updatedFileName);
            // 수정된 파일 저장
            fileService.uploadFile(imgFile, updatedFileName);
            wasteRepository.flush();
            // 저장된 파일 삭제
            fileService.deleteFileIfExists(savedFileName);
        }

        return WasteDto.fromEntity(savedWaste, memberPrincipal);
    }

    public void deleteWaste(Long wasteId) {
        String fileName = findFileNameOfWaste(wasteId);
        wasteRepository.deleteById(wasteId);
        // 파일 삭제
        fileService.deleteFileIfExists(fileName);
    }

    @Transactional(readOnly = true)
    public String findFileNameOfWaste(Long wasteId) {
        return wasteRepository
                .findFileNameById(wasteId)
                .orElseThrow(() -> new WasteException(ErrorCode.NOT_FOUND_WASTE));
    }

    /**
     * 데이터가 수정되었는지 확인
     */
    private <T> boolean isUpdatedArticleData(T savedData, T updatedData) {
        return !Objects.equals(savedData, updatedData);
    }

    /**
     * 작성자인지 확인
     */
    public boolean isWriterOfArticle(Long wasteId, Long memberId) {
        return wasteRepository.existsByIdAndMember_Id(wasteId, memberId);
    }
}
