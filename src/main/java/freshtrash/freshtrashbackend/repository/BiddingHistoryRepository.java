package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.BiddingHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional(propagation = Propagation.SUPPORTS)
public interface BiddingHistoryRepository extends JpaRepository<BiddingHistory, Long> {
    @EntityGraph(attributePaths = {"auction", "member"})
    Optional<BiddingHistory> findFirstByAuctionIdAndMemberIdOrderByPriceDesc(Long auctionId, Long memberId);

    @Modifying
    @Query("update BiddingHistory bh set bh.successBidAt = current_timestamp where bh.auctionId = ?1")
    void updateSuccessBidAtByAuctionId(Long auctionId);
}
