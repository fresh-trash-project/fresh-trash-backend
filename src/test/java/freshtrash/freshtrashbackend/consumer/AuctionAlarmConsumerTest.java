package freshtrash.freshtrashbackend.consumer;

import com.rabbitmq.client.Channel;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.dto.request.AuctionAlarmPayload;
import freshtrash.freshtrashbackend.dto.request.BaseAlarmPayload;
import freshtrash.freshtrashbackend.dto.response.AlarmResponse;
import freshtrash.freshtrashbackend.entity.Alarm;
import freshtrash.freshtrashbackend.service.AlarmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuctionAlarmConsumerTest {
    @InjectMocks
    private AuctionAlarmConsumer auctionAlarmConsumer;

    @Mock
    private AlarmService alarmService;

    @Test
    @DisplayName("경매 알람 메시지 전송 consumer")
    void consumeAuctionMessage() {
        // given
        BaseAlarmPayload alarmPayload = FixtureDto.createAlarmPayload();
        Alarm alarm = Alarm.fromAlarmPayload(alarmPayload);
        Channel channel = mock(Channel.class);
        long deliveryTag = 3;
        given(alarmService.saveAlarm(alarmPayload)).willReturn(alarm);
        willDoNothing().given(alarmService).receive(alarmPayload.getMemberId(), AlarmResponse.fromEntity(alarm));
        // when
        auctionAlarmConsumer.consumeAuctionMessage(channel, deliveryTag, alarmPayload);
        ArgumentCaptor<AuctionAlarmPayload> alarmCaptor = ArgumentCaptor.forClass(AuctionAlarmPayload.class);
        // then
        verify(alarmService, times(1)).saveAlarm(alarmCaptor.capture());
        verify(alarmService, times(1)).receive(alarmPayload.getMemberId(), AlarmResponse.fromEntity(alarm));
    }
}