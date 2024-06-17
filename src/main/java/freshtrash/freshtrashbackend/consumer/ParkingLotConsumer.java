package freshtrash.freshtrashbackend.consumer;

import com.rabbitmq.client.Channel;
import freshtrash.freshtrashbackend.aspect.annotation.ManualAcknowledge;
import freshtrash.freshtrashbackend.dto.request.BaseAlarmPayload;
import freshtrash.freshtrashbackend.service.SlackService;
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
public class ParkingLotConsumer {
    private final SlackService slackService;

    /**
     * Parking Lot Listener
     */
    @ManualAcknowledge
    @RabbitListener(
            queues = {"#{productParkingLotQueue.name}", "#{chatParkingLotQueue.name}", "#{auctionParkingLotQueue.name}"
            })
    public void handleProductParkingLot(
            Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag, @Payload BaseAlarmPayload alarmPayload) {
        log.warn("consume from parking lot");
        slackService.sendMessage(alarmPayload.getMessage(), alarmPayload.toMap());
        log.debug("send notification to slack");
    }
}
