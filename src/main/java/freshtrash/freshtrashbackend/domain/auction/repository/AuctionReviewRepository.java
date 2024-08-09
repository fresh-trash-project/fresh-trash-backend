package freshtrash.freshtrashbackend.domain.auction.repository;

import freshtrash.freshtrashbackend.domain.auction.entity.AuctionReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.SUPPORTS)
public interface AuctionReviewRepository extends JpaRepository<AuctionReview, Long> {
    boolean existsByAuctionId(Long auctionId);
}
