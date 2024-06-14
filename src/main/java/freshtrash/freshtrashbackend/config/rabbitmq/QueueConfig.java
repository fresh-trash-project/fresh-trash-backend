package freshtrash.freshtrashbackend.config.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static freshtrash.freshtrashbackend.config.rabbitmq.QueueType.*;
import static freshtrash.freshtrashbackend.config.rabbitmq.QueueType.CHAT_PARKING_LOT;

@Configuration
public class QueueConfig {
    /**
     * Queue with DLQ
     */
    @Bean
    Queue productCompleteQueue() {
        return createQueueWithDLQ(PRODUCT_TRANSACTION_COMPLETE, DLQ_PRODUCT_TRANSACTION_COMPLETE);
    }

    @Bean
    Queue productFlagQueue() {
        return createQueueWithDLQ(PRODUCT_TRANSACTION_FLAG, DLQ_PRODUCT_TRANSACTION_FLAG);
    }

    @Bean
    Queue productChangeStatusQueue() {
        return createQueueWithDLQ(PRODUCT_CHANGE_SELL_STATUS, DLQ_PRODUCT_CHANGE_SELL_STATUS);
    }

    @Bean
    Queue chatQueue() {
        return createQueueWithDLQ(CHAT, DLQ_CHAT);
    }

    /**
     * DLQ
     */
    @Bean
    Queue dlqProductCompleteQueue() {
        return createQueue(DLQ_PRODUCT_TRANSACTION_COMPLETE);
    }

    @Bean
    Queue dlqProductFlagQueue() {
        return createQueue(DLQ_PRODUCT_TRANSACTION_FLAG);
    }

    @Bean
    Queue dlqProductChangeStatusQueue() {
        return createQueue(DLQ_PRODUCT_CHANGE_SELL_STATUS);
    }

    @Bean
    Queue dlqChatQueue() {
        return createQueue(DLQ_CHAT);
    }

    /**
     * Parking Lot Queue
     */
    @Bean
    Queue productParkingLotQueue() {
        return createQueue(PRODUCT_PARKING_LOT);
    }

    @Bean
    Queue chatParkingLotQueue() {
        return createQueue(CHAT_PARKING_LOT);
    }

    private Queue createQueueWithDLQ(QueueType queueType, QueueType dlqType) {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-queue-version", 2);
        args.put("x-dead-letter-exchange", RabbitMQConfig.DLQ_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", dlqType.getRoutingKey());
        return new Queue(queueType.getName(), true, false, false, args);
    }

    private Queue createQueue(QueueType queueType) {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-queue-version", 2);
        return new Queue(queueType.getName(), true, false, false, args);
    }
}
