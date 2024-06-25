package freshtrash.freshtrashbackend.service.producer.publisher;

import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.dto.events.AlarmEvent;
import freshtrash.freshtrashbackend.dto.request.BaseAlarmPayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.willDoNothing;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class RabbitMQPublisherTest {
    @InjectMocks
    private RabbitMQPublisher rabbitMQPublisher;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Test
    @DisplayName("BaseEvent 객체를 받아 메시지를 전송한다.")
    void given_baseEvent_when_then_publishMessage() {
        // given
        BaseAlarmPayload alarmPayload = FixtureDto.createAlarmPayload();
        AlarmEvent alarmEvent = new AlarmEvent("routingKey", alarmPayload);
        willDoNothing().given(rabbitTemplate).convertAndSend(alarmEvent.getRoutingKey(), alarmEvent.getPayload());
        // when
        assertThatCode(() -> rabbitMQPublisher.publish(alarmEvent)).doesNotThrowAnyException();
        // then
    }
}