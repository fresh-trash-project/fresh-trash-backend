package freshtrash.freshtrashbackend.integration;

import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.service.AuctionEventService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@Disabled
@ActiveProfiles("integration_test")
@SpringBootTest
@Import(TestSecurityConfig.class)
public class NotPaidAuctionIntegrationTest {
    @Autowired
    private AuctionEventService auctionEventService;

    @Test
    @DisplayName("매일 0시에 낙찰되었지만 24시간 지난 입찰 내역을 조회하여 결제가 안되었으면 경매를 취소하고 알림 전송한다.")
    void processNotPaidAuctions() {
        auctionEventService.processNotPaidAuctions();
    }
}
