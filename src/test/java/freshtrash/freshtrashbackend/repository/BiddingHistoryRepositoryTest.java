package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.BiddingHistory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@SpringBootTest
class BiddingHistoryRepositoryTest {
    @Autowired
    private BiddingHistoryRepository biddingHistoryRepository;

    @Test
    void findByAuctionIdAndMemberId() {
        Long auctionId = 2L, memberId = 1L;
        BiddingHistory biddingHistory = biddingHistoryRepository
                .findFirstByAuctionIdAndMemberIdOrderByPriceDesc(auctionId, memberId)
                .get();
        assertThat(biddingHistory.getPrice()).isEqualTo(2000);
    }
}