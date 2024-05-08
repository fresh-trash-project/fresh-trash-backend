package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.response.WasteResponse;
import freshtrash.freshtrashbackend.entity.WasteLike;
import freshtrash.freshtrashbackend.exception.WasteException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.WasteLikeRepository;
import freshtrash.freshtrashbackend.repository.WasteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WasteLikeService {
    private final WasteLikeRepository wasteLikeRepository;
    private final WasteRepository wasteRepository;

    public Page<WasteResponse> getLikedWastes(Long memberId, Pageable pageable) {
        return wasteLikeRepository
                .findAllByMember_Id(memberId, pageable)
                .map(WasteLike::getWaste)
                .map(WasteResponse::fromEntity);
    }

    @Transactional
    public void addWasteLike(Long memberId, Long wasteId) {
        if (wasteLikeRepository.existsByMemberIdAndWasteId(memberId, wasteId)) {
            throw new WasteException(ErrorCode.ALREADY_EXISTS_LIKE);
        }

        wasteLikeRepository.save(WasteLike.of(memberId, wasteId));
        wasteRepository.updateLikeCount(wasteId, 1);
    }

    @Transactional
    public void deleteWasteLike(Long memberId, Long wasteId) {
        if (!wasteLikeRepository.existsByMemberIdAndWasteId(memberId, wasteId)) {
            throw new WasteException(ErrorCode.NOT_FOUND_LIKE);
        }

        wasteLikeRepository.deleteByMemberIdAndWasteId(memberId, wasteId);
        wasteRepository.updateLikeCount(wasteId, -1);
    }
}
