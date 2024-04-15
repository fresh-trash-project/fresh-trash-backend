package freshtrash.freshtrashbackend.service;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.dto.WasteDto;
import freshtrash.freshtrashbackend.dto.constants.LikeStatus;
import freshtrash.freshtrashbackend.dto.request.ReviewRequest;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.entity.WasteLike;
import freshtrash.freshtrashbackend.entity.WasteReview;
import freshtrash.freshtrashbackend.exception.ReviewException;
import freshtrash.freshtrashbackend.exception.WasteException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.WasteLikeRepository;
import freshtrash.freshtrashbackend.repository.WasteRepository;
import freshtrash.freshtrashbackend.repository.WasteReviewRepository;
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
    private final WasteReviewRepository wasteReviewRepository;
    private final WasteLikeRepository wasteLikeRepository;

    public Waste getWaste(Long wasteId) {
        return wasteRepository.findById(wasteId).orElseThrow(() -> new WasteException(ErrorCode.NOT_FOUND_WASTE));
    }

    public Page<WasteDto> getWastes(String district, Predicate predicate, Pageable pageable) {
        return wasteRepository.findAll(district, predicate, pageable).map(WasteDto::fromEntity);
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

    /**
     * 폐기물 리뷰 작성
     */
    public WasteReview insertWasteReview(ReviewRequest reviewRequest, Long wasteId, Long memberId) {
        // 이미 리뷰가 등록되있는지 확인
        boolean exists = wasteReviewRepository.existsByWasteId(wasteId);
        if (exists) {
            throw new ReviewException(ErrorCode.ALREADY_EXISTS_REVIEW);
        }

        WasteReview wasteReview = reviewRequest.toEntity(wasteId, memberId);
        return wasteReviewRepository.save(wasteReview);
    }

    /**
     * 관심폐기물 표시 또는 제거
     */
    @Transactional
    public int addOrDeleteWasteLike(LikeStatus likeStatus, Long memberId, Long wasteId) {
        int updateCount = 0;

        isPossibleLikeUpdate(likeStatus, memberId, wasteId);

        if (likeStatus == LikeStatus.LIKE) {
            wasteLikeRepository.save(WasteLike.of(memberId, wasteId));
            updateCount = 1;
        } else if (likeStatus == LikeStatus.UNLIKE) {
            wasteLikeRepository.deleteByMemberIdAndWasteId(memberId, wasteId);
            updateCount = -1;
        }

        // update likeCount
        return wasteRepository.updateLikeCount(wasteId, updateCount);
    }

    /**
     * 관심추가 또는 삭제가 가능한지 체크
     * (likeStatus가 LIKE -> 관심추가된 데이터가 없어야하고, UNLIKE -> 관심추가된 데이터가 있어야한다)
     */
    public void isPossibleLikeUpdate(LikeStatus likeStatus, Long memberId, Long wasteId) {
        boolean existsLike = wasteLikeRepository.existsByMemberIdAndWasteId(memberId, wasteId);

        if ((likeStatus == LikeStatus.LIKE && existsLike) || (likeStatus == LikeStatus.UNLIKE && !existsLike)) {
            throw new WasteException(ErrorCode.UN_MATCHED_LIKE_STATUS);
        }
    }
}