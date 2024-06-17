package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.BiddingHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(propagation = Propagation.SUPPORTS)
public interface BiddingHistoryRepository extends JpaRepository<BiddingHistory, Long> {
    @EntityGraph(attributePaths = {"auction", "member"})
    Optional<BiddingHistory> findFirstByAuctionIdAndMemberIdOrderByPriceDesc(Long auctionId, Long memberId);
}
