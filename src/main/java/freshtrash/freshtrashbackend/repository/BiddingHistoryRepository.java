package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.BiddingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(propagation = Propagation.SUPPORTS)
public interface BiddingHistoryRepository extends JpaRepository<BiddingHistory, Long> {
    List<BiddingHistory> findAllByAuctionId(Long auctionId);
}
