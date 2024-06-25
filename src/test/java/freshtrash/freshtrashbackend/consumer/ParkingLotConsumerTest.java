package freshtrash.freshtrashbackend.consumer;

import com.rabbitmq.client.Channel;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.dto.request.BaseAlarmPayload;
import freshtrash.freshtrashbackend.service.SlackService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ParkingLotConsumerTest {
    @InjectMocks
    private ParkingLotConsumer parkingLotConsumer;

    @Mock
    private SlackService slackService;

    @Test
    @DisplayName("DLQ에서 retry 실패한 메시지가 전달된 parking lot queue에 있는 메시지를 받아 slack에 알림 전송한다.")
    void handleProductParkingLot_sendSlackNotification() {
        // given
        BaseAlarmPayload alarmPayload = FixtureDto.createAlarmPayload();
        Channel channel = mock(Channel.class);
        long deliveryTag = 3;
        willDoNothing().given(slackService).sendMessage(alarmPayload.getMessage(), alarmPayload.toMap());
        // when
        parkingLotConsumer.handleProductParkingLot(channel, deliveryTag, alarmPayload);
        // then
    }
}