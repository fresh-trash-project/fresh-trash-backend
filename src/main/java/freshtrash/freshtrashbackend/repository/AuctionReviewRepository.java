package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.AuctionReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionReviewRepository extends JpaRepository<AuctionReview, Long> {
    boolean existsByAuctionId(Long auctionId);
}
