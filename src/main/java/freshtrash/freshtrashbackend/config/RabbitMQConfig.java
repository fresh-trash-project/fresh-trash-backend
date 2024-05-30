package freshtrash.freshtrashbackend.config;

import freshtrash.freshtrashbackend.config.constants.QueueType;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static freshtrash.freshtrashbackend.config.constants.QueueType.*;

@Configuration
public class RabbitMQConfig {
    private static final String directExchangeName = "direct-exchange";
    private static final String topicExchangeName = "amq.topic";

    @Bean
    Queue wasteCompleteQueue() {
        return createQueue(WASTE_TRANSACTION_COMPLETE);
    }

    @Bean
    Queue wasteFlagQueue() {
        return createQueue(WASTE_TRANSACTION_FLAG);
    }

    @Bean
    Queue wasteChangeStatusQueue() {
        return createQueue(WASTE_CHANGE_SELL_STATUS);
    }

    @Bean
    Queue chatQueue() {
        return createQueue(CHAT);
    }

    @Bean
    Binding wasteCompleteBinding(Queue wasteCompleteQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(wasteCompleteQueue)
                .to(directExchange)
                .with(WASTE_TRANSACTION_COMPLETE.getRoutingKey());
    }

    @Bean
    Binding wasteCancelBinding(Queue wasteFlagQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(wasteFlagQueue).to(directExchange).with(WASTE_TRANSACTION_FLAG.getRoutingKey());
    }

    @Bean
    Binding wasteChangeStatusBinding(Queue wasteChangeStatusQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(wasteChangeStatusQueue)
                .to(directExchange)
                .with(WASTE_CHANGE_SELL_STATUS.getRoutingKey());
    }

    @Bean
    Binding topicBinding(Queue chatQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(chatQueue).to(topicExchange).with(CHAT.getRoutingKey());
    }

    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(directExchangeName);
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(directExchangeName);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
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
