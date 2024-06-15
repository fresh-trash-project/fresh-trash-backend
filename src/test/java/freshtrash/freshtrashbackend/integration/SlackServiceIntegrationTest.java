package freshtrash.freshtrashbackend.integration;

import freshtrash.freshtrashbackend.service.SlackService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Disabled
@SpringBootTest
@ActiveProfiles("integration_test")
class SlackServiceIntegrationTest {
    @Autowired
    private SlackService slackService;

    @Test
    @DisplayName("Slack 메시지 전송")
    void sendMessage() {
        // given
        String title = "Slack Test from spring";
        Map<String, String> data = new HashMap<>();
        data.put("title", "content");
        // when
        slackService.sendMessage(title, data);
        // then
    }
}