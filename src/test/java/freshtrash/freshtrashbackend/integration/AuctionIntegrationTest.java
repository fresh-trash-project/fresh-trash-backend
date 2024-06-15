package freshtrash.freshtrashbackend.integration;

import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.controller.AuctionApi;
import freshtrash.freshtrashbackend.dto.request.BiddingRequest;
import freshtrash.freshtrashbackend.repository.AuctionRepository;
import freshtrash.freshtrashbackend.service.AuctionEventService;
import freshtrash.freshtrashbackend.service.AuctionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Disabled
@ActiveProfiles("integration_test")
@SpringBootTest
@Import(TestSecurityConfig.class)
public class AuctionIntegrationTest {
    @Autowired
    AuctionApi auctionApi;

    @Autowired
    AuctionService auctionService;

    @Autowired
    AuctionEventService auctionEventService;

    @Autowired
    AuctionRepository auctionRepository;

    @Test
    @DisplayName("입찰")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void bidding() throws InterruptedException {
        // given
        int numThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        Random random = new Random();
        Long auctionId = 2L;
        // when
        for (int i = 1; i <= numThreads; i++) {
            executorService.execute(() -> {
                try {
                    int price = random.nextInt((5000 - 1000) + 1) + 1000;
                    price -= price % 10;
                    log.info("Bidding price: {}", price);
                    auctionApi.placeBidding(auctionId, new BiddingRequest(price), FixtureDto.createMemberPrincipal());
                    latch.countDown();
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        // then
        Thread.sleep(2000);
        int finalBiddingPrice = auctionService.getAuction(auctionId).getFinalBid();
        log.info("final bidding price: {}", finalBiddingPrice);
    }

    @Test
    @DisplayName("[Schedule] 매일 0시에 마감된 경매 낙찰 처리")
    void completeAuction() {
        // given
        // 마감된 되었지만 status가 ONGOING인 경매 수
        int previousCount = auctionRepository.findAllEndedAuctions().size();
        // when
        auctionEventService.completeAuction();
        // then
        assertThat(auctionRepository.findAllEndedAuctions().size()).isLessThan(previousCount);
    }
}
