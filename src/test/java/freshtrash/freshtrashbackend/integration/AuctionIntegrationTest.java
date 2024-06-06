package freshtrash.freshtrashbackend.integration;

import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.controller.AuctionApi;
import freshtrash.freshtrashbackend.dto.request.BiddingRequest;
import freshtrash.freshtrashbackend.service.AuctionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@SpringBootTest
@Import(TestSecurityConfig.class)
public class AuctionIntegrationTest {
    @Autowired
    AuctionApi auctionApi;

    @Autowired
    AuctionService auctionService;

    @Test
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void bidding() {
        // given
        int numThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        Random random = new Random();
        Long auctionId = 2L;
        // when
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                int price = random.nextInt((5000 - 1000) + 1) + 1000;
                log.info("Bidding price: {}", price);
                auctionApi.placeBidding(auctionId, new BiddingRequest(price), FixtureDto.createMemberPrincipal());
                latch.countDown();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        // then
        int finalBiddingPrice = auctionService.getAuction(auctionId).getFinalBid();
        log.info("final bidding price: {}", finalBiddingPrice);
    }
}
