package freshtrash.freshtrashbackend.consumer;

import com.rabbitmq.client.Channel;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.dto.request.BaseAlarmPayload;
import freshtrash.freshtrashbackend.dto.request.ProductAlarmPayload;
import freshtrash.freshtrashbackend.entity.Alarm;
import freshtrash.freshtrashbackend.repository.EmitterRepository;
import freshtrash.freshtrashbackend.service.AlarmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ProductAlarmConsumerTest {
    @InjectMocks
    private ProductAlarmConsumer productAlarmConsumer;

    @Mock
    private EmitterRepository emitterRepository;

    @Mock
    private AlarmService alarmService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Test
    @DisplayName("알람 메시지 consume")
    void given_alarmPayload_when_listenMessage_then_saveAlarmAndSendAlarm() {
        // given
        BaseAlarmPayload baseAlarmPayload = FixtureDto.createAlarmPayload();
        Long memberId = baseAlarmPayload.getMemberId();
        Alarm alarm = Alarm.fromAlarmPayload(baseAlarmPayload);
        SseEmitter sseEmitter = new SseEmitter(TimeUnit.MINUTES.toMillis(30));
        Channel channel = mock(Channel.class);
        long deliveryTag = 3;
        given(alarmService.saveAlarm(eq(baseAlarmPayload))).willReturn(alarm);
        given(emitterRepository.findByMemberId(eq(memberId))).willReturn(Optional.of(sseEmitter));
        // whenxp
        productAlarmConsumer.consumeProductDealMessage(channel, deliveryTag, baseAlarmPayload);
        ArgumentCaptor<ProductAlarmPayload> alarmCaptor = ArgumentCaptor.forClass(ProductAlarmPayload.class);
        // then
        verify(alarmService, times(1)).saveAlarm(alarmCaptor.capture());
        verify(emitterRepository, times(1)).findByMemberId(eq(memberId));
    }
}
