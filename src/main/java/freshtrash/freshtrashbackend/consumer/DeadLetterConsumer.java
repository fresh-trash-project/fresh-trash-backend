package freshtrash.freshtrashbackend.consumer;

import com.rabbitmq.client.Channel;
import freshtrash.freshtrashbackend.aspect.annotation.ManualAcknowledge;
import freshtrash.freshtrashbackend.config.rabbitmq.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeadLetterConsumer {
    private final RabbitTemplate rabbitTemplate;

    @ManualAcknowledge
    @RabbitListener(
            queues = {
                "#{dlqProductCompleteQueue.name}",
                "#{dlqProductFlagQueue.name}",
                "#{dlqProductChangeStatusQueue.name}",
                "#{dlqChatQueue.name}",
                "#{dlqAuctionCompleteQueue.name}",
                "#{dlqCancelAuctionQueue.name}",
                "#{dlqAuctionPayQueue.name}",
                "#{dlqReviewQueue.name}"
            })
    public void handleFailedProductDealMessage(
            Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag, Message message) {
        log.debug("consume from dead letter queue");
        log.debug("message: {}", message);

        Integer retriesCnt =
                (Integer) message.getMessageProperties().getHeaders().get(RabbitMQConfig.HEADER_X_RETRIES_COUNT);
        if (retriesCnt == null) retriesCnt = 1;
        else retriesCnt++;

        if (retriesCnt > RabbitMQConfig.RETRIES_COUNT) {
            log.debug("Sending message to the parking lot queue");
            rabbitTemplate.send(
                    RabbitMQConfig.PARKING_LOT_EXCHANGE_NAME,
                    message.getMessageProperties().getReceivedRoutingKey(),
                    message);
            return;
        }

        log.debug("Retrying message for the {} time", retriesCnt);
        message.getMessageProperties().getHeaders().put(RabbitMQConfig.HEADER_X_RETRIES_COUNT, retriesCnt);
        rabbitTemplate.send(
                RabbitMQConfig.TOPIC_EXCHANGE_NAME,
                message.getMessageProperties().getReceivedRoutingKey(),
                message);
    }
}
