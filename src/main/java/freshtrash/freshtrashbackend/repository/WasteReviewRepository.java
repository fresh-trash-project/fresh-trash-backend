package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.WasteReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WasteReviewRepository extends JpaRepository<WasteReview, Long> {
    boolean existsByWasteIdAndMemberId(Long wasteId, Long memberId);
}
