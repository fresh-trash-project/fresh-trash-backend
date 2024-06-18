package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.AuctionReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.SUPPORTS)
public interface AuctionReviewRepository extends JpaRepository<AuctionReview, Long> {
    boolean existsByAuctionId(Long auctionId);
}
