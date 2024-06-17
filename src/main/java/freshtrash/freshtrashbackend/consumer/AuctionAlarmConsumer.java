package freshtrash.freshtrashbackend.consumer;

import com.rabbitmq.client.Channel;
import freshtrash.freshtrashbackend.aspect.annotation.ManualAcknowledge;
import freshtrash.freshtrashbackend.dto.request.BaseAlarmPayload;
import freshtrash.freshtrashbackend.dto.response.AlarmResponse;
import freshtrash.freshtrashbackend.entity.Alarm;
import freshtrash.freshtrashbackend.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionAlarmConsumer {
    private final AlarmService alarmService;

    /**
     * 경매 알람 메시지 전송 Listener
     */
    @ManualAcknowledge
    @RabbitListener(queues = {"#{auctionCompleteQueue.name}", "#{cancelAuctionQueue.name}", "#{auctionPayQueue.name}"})
    public void consumeAuctionMessage(
            Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag, @Payload BaseAlarmPayload alarmPayload) {
        log.debug("receive complete bid auction message: {}", alarmPayload);
        Alarm alarm = alarmService.saveAlarm(alarmPayload);
        alarmService.receive(alarmPayload.getMemberId(), AlarmResponse.fromEntity(alarm));
    }
}
