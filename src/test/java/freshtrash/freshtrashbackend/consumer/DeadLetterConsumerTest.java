package freshtrash.freshtrashbackend.consumer;

import com.rabbitmq.client.Channel;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.config.rabbitmq.RabbitMQConfig;
import freshtrash.freshtrashbackend.dto.request.BaseAlarmPayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class DeadLetterConsumerTest {
    @InjectMocks private DeadLetterConsumer deadLetterConsumer;
    @Mock private RabbitTemplate rabbitTemplate;

    @Test
    @DisplayName("DLQ로 전달된 실패한 메시지는 consumer에서 retry를 시도한다.")
    void handleFailedMessage() {
        //given
        Message message = MessageBuilder.withBody("message".getBytes(StandardCharsets.UTF_8)).build();
        Channel channel = mock(Channel.class);
        long deliveryTag = 3;
        willDoNothing().given(rabbitTemplate).send(RabbitMQConfig.TOPIC_EXCHANGE_NAME, message.getMessageProperties().getReceivedRoutingKey(), message);
        //when
        deadLetterConsumer.handleFailedMessage(channel, deliveryTag, message);
        //then
        Integer retriesCnt =
                (Integer) message.getMessageProperties().getHeaders().get(RabbitMQConfig.HEADER_X_RETRIES_COUNT);
        assertThat(retriesCnt).isEqualTo(1);
    }

    @Test
    @DisplayName("DLQ로 전달된 실패한 메시지는 consumer에서 최대 retry 횟수를 넘기면 parking lot queue로 전달한다.")
    void handleFailedMessageAndExceedRetryCounts() {
        //given
        Message message = MessageBuilder.withBody("message".getBytes(StandardCharsets.UTF_8)).build();
        message.getMessageProperties().getHeaders().put(RabbitMQConfig.HEADER_X_RETRIES_COUNT, 3);
        Channel channel = mock(Channel.class);
        long deliveryTag = 3;
        willDoNothing().given(rabbitTemplate).send(RabbitMQConfig.PARKING_LOT_EXCHANGE_NAME, message.getMessageProperties().getReceivedRoutingKey(), message);
        //when
        deadLetterConsumer.handleFailedMessage(channel, deliveryTag, message);
        //then
        Integer retriesCnt =
                (Integer) message.getMessageProperties().getHeaders().get(RabbitMQConfig.HEADER_X_RETRIES_COUNT);
        assertThat(retriesCnt).isEqualTo(3);
    }
}