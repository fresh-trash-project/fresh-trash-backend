package freshtrash.freshtrashbackend.config.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static freshtrash.freshtrashbackend.config.rabbitmq.QueueType.*;

@Configuration
public class QueueConfig {
    private static final String QUEUE_VERSION = "x-queue-version";
    private static final String DLX = "x-dead-letter-exchange";
    private static final String DLK = "x-dead-letter-routing-key";

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

    @Bean
    Queue auctionCompleteQueue() {
        return createQueueWithDLQ(AUCTION_BID_COMPLETE, DLQ_AUCTION_BID_COMPLETE);
    }

    @Bean
    Queue cancelAuctionQueue() {
        return createQueueWithDLQ(CANCEL_AUCTION, DLQ_CANCEL_AUCTION);
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

    @Bean
    Queue dlqAuctionCompleteQueue() {
        return createQueue(DLQ_AUCTION_BID_COMPLETE);
    }

    @Bean
    Queue dlqCancelAuctionQueue() {
        return createQueue(DLQ_CANCEL_AUCTION);
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

    @Bean
    Queue auctionParkingLotQueue() {
        return createQueue(AUCTION_PARKING_LOT);
    }

    private Queue createQueueWithDLQ(QueueType queueType, QueueType dlqType) {
        Map<String, Object> args = new HashMap<>();
        args.put(QUEUE_VERSION, 2);
        args.put(DLX, RabbitMQConfig.DLQ_EXCHANGE_NAME);
        args.put(DLK, dlqType.getRoutingKey());
        return new Queue(queueType.getName(), true, false, false, args);
    }

    private Queue createQueue(QueueType queueType) {
        Map<String, Object> args = new HashMap<>();
        args.put(QUEUE_VERSION, 2);
        return new Queue(queueType.getName(), true, false, false, args);
    }
}
