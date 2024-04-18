package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.WasteReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.SUPPORTS)
public interface WasteReviewRepository extends JpaRepository<WasteReview, Long> {
    boolean existsByWasteId(Long wasteId);
}
