package freshtrash.freshtrashbackend.config;

import freshtrash.freshtrashbackend.config.constants.QueueType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static freshtrash.freshtrashbackend.config.constants.QueueType.*;

@Slf4j
@Configuration
public class RabbitMQConfig {
    private static final String TOPIC_EXCHANGE_NAME = "amq.topic";

    @Bean
    Queue productCompleteQueue() {
        return createQueue(PRODUCT_TRANSACTION_COMPLETE);
    }

    @Bean
    Queue productFlagQueue() {
        return createQueue(PRODUCT_TRANSACTION_FLAG);
    }

    @Bean
    Queue productChangeStatusQueue() {
        return createQueue(PRODUCT_CHANGE_SELL_STATUS);
    }

    @Bean
    Queue chatQueue() {
        return createQueue(CHAT);
    }

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
    Binding topicBinding(Queue chatQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(chatQueue).to(topicExchange).with(CHAT.getRoutingKey());
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(TOPIC_EXCHANGE_NAME);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
        rabbitTemplate.setMandatory(true);
        // 메시지가 브로커에 도착했지만 지정된 큐로 라우팅되지 못한 경우
        rabbitTemplate.setReturnsCallback((returnedMessage) -> {
            log.info("routingKey: {}, replyText: {}", returnedMessage.getRoutingKey(), returnedMessage.getReplyText());
        });
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    private Queue createQueue(QueueType queueType) {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-queue-version", 2);
        return new Queue(queueType.getName(), true, false, false, args);
    }
}
