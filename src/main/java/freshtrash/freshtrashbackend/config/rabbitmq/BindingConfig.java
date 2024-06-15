package freshtrash.freshtrashbackend.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static freshtrash.freshtrashbackend.config.rabbitmq.QueueType.*;

@Configuration
public class BindingConfig {
    @Bean
    Binding productCompleteBinding(Queue productCompleteQueue, TopicExchange topicExchange) {
        return createBinding(productCompleteQueue, topicExchange, PRODUCT_TRANSACTION_COMPLETE.getRoutingKey());
    }

    @Bean
    Binding productFlagBinding(Queue productFlagQueue, TopicExchange topicExchange) {
        return createBinding(productFlagQueue, topicExchange, PRODUCT_TRANSACTION_FLAG.getRoutingKey());
    }

    @Bean
    Binding productChangeStatusBinding(Queue productChangeStatusQueue, TopicExchange topicExchange) {
        return createBinding(productChangeStatusQueue, topicExchange, PRODUCT_CHANGE_SELL_STATUS.getRoutingKey());
    }

    @Bean
    Binding chatBinding(Queue chatQueue, TopicExchange topicExchange) {
        return createBinding(chatQueue, topicExchange, CHAT.getRoutingKey());
    }

    @Bean
    Binding dlqProductCompleteBinding(Queue dlqProductCompleteQueue, TopicExchange dlqExchange) {
        return createBinding(dlqProductCompleteQueue, dlqExchange, DLQ_PRODUCT_TRANSACTION_COMPLETE.getRoutingKey());
    }

    @Bean
    Binding dlqProductFlagBinding(Queue dlqProductFlagQueue, TopicExchange dlqExchange) {
        return createBinding(dlqProductFlagQueue, dlqExchange, DLQ_PRODUCT_TRANSACTION_FLAG.getRoutingKey());
    }

    @Bean
    Binding dlqProductChangeStatusBinding(Queue dlqProductChangeStatusQueue, TopicExchange dlqExchange) {
        return createBinding(dlqProductChangeStatusQueue, dlqExchange, DLQ_PRODUCT_CHANGE_SELL_STATUS.getRoutingKey());
    }

    @Bean
    Binding dlqChatBinding(Queue dlqChatQueue, TopicExchange dlqExchange) {
        return createBinding(dlqChatQueue, dlqExchange, DLQ_CHAT.getRoutingKey());
    }

    @Bean
    Binding productParkingLotBinding(Queue productParkingLotQueue, TopicExchange parkingLotExchange) {
        return createBinding(productParkingLotQueue, parkingLotExchange, PRODUCT_PARKING_LOT.getRoutingKey());
    }

    @Bean
    Binding chatParkingLotBinding(Queue chatParkingLotQueue, TopicExchange parkingLotExchange) {
        return createBinding(chatParkingLotQueue, parkingLotExchange, CHAT_PARKING_LOT.getRoutingKey());
    }

    private Binding createBinding(Queue queue, TopicExchange exchange, String routingKey) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }
}
