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
        return BindingBuilder.bind(productCompleteQueue)
                .to(topicExchange)
                .with(PRODUCT_TRANSACTION_COMPLETE.getRoutingKey());
    }

    @Bean
    Binding productCancelBinding(Queue productFlagQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(productFlagQueue).to(topicExchange).with(PRODUCT_TRANSACTION_FLAG.getRoutingKey());
    }

    @Bean
    Binding productChangeStatusBinding(Queue productChangeStatusQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(productChangeStatusQueue)
                .to(topicExchange)
                .with(PRODUCT_CHANGE_SELL_STATUS.getRoutingKey());
    }

    @Bean
    Binding chatBinding(Queue chatQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(chatQueue).to(topicExchange).with(CHAT.getRoutingKey());
    }

    @Bean
    Binding dlqProductCompleteBinding(Queue dlqProductCompleteQueue, TopicExchange dlqExchange) {
        return BindingBuilder.bind(dlqProductCompleteQueue)
                .to(dlqExchange)
                .with(DLQ_PRODUCT_TRANSACTION_COMPLETE.getRoutingKey());
    }

    @Bean
    Binding dlqProductFlagBinding(Queue dlqProductFlagQueue, TopicExchange dlqExchange) {
        return BindingBuilder.bind(dlqProductFlagQueue)
                .to(dlqExchange)
                .with(DLQ_PRODUCT_TRANSACTION_FLAG.getRoutingKey());
    }

    @Bean
    Binding dlqProductChangeStatusBinding(Queue dlqProductChangeStatusQueue, TopicExchange dlqExchange) {
        return BindingBuilder.bind(dlqProductChangeStatusQueue)
                .to(dlqExchange)
                .with(DLQ_PRODUCT_CHANGE_SELL_STATUS.getRoutingKey());
    }

    @Bean
    Binding dlqChatBinding(Queue dlqChatQueue, TopicExchange dlqExchange) {
        return BindingBuilder.bind(dlqChatQueue).to(dlqExchange).with(DLQ_CHAT.getRoutingKey());
    }

    @Bean
    Binding productParkingLotBinding(Queue productParkingLotQueue, TopicExchange parkingLotExchange) {
        return BindingBuilder.bind(productParkingLotQueue)
                .to(parkingLotExchange)
                .with(PRODUCT_PARKING_LOT.getRoutingKey());
    }

    @Bean
    Binding chatParkingLotBinding(Queue chatParkingLotQueue, TopicExchange parkingLotExchange) {
        return BindingBuilder.bind(chatParkingLotQueue).to(parkingLotExchange).with(CHAT_PARKING_LOT.getRoutingKey());
    }
}
