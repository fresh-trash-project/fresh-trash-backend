package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.WasteLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WasteLikeRepository extends JpaRepository<WasteLike, Long> {
    boolean existsByMemberIdAndWasteId(Long memberId, Long wasteId);

    void deleteByMemberIdAndWasteId(Long memberId, Long wasteId);
}
