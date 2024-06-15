package freshtrash.freshtrashbackend.service;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import com.slack.api.webhook.WebhookPayloads;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackService {
    @Value("${webhook.slack.url}")
    private String SLACK_WEBHOOK_URL;

    private final Slack slackClient;

    /**
     * 슬랙 메시지 전송
     */
    public void sendMessage(String title, Map<String, String> data) {
        try {
            slackClient.send(SLACK_WEBHOOK_URL, WebhookPayloads.payload(payloadBuilder -> payloadBuilder
                    .text(title)
                    .attachments(List.of(Attachment.builder()
                            .color(Color.RED.getCode())
                            .fields(data.keySet().stream()
                                    .map(key -> generateSlackField(key, data.get(key)))
                                    .collect(Collectors.toList()))
                            .build()))));
        } catch (IOException e) {
            log.error("슬랙 메시지 전송 실패! title: {}, data: {}", title, data, e);
        }
    }

    private Field generateSlackField(String title, String value) {
        return Field.builder().title(title).value(value).valueShortEnough(false).build();
    }

    @Getter
    private enum Color {
        GREEN("#36a64f"),
        RED("#ff0000"),
        BLUE("#0000ff"),
        YELLOW("#ffff00"),
        BLACK("#000000"),
        WHITE("#ffffff");

        private final String code;

        Color(String color) {
            this.code = color;
        }
    }
}
