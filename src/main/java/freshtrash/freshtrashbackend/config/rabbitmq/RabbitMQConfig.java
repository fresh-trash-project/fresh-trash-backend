package freshtrash.freshtrashbackend.config.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMQConfig {
    public static final String TOPIC_EXCHANGE_NAME = "amq.topic";
    public static final String DLQ_EXCHANGE_NAME = "topic.dlx";
    public static final String PARKING_LOT_EXCHANGE_NAME = "topic.parking-lot";
    public static final String HEADER_X_RETRIES_COUNT = "x-retries-count";
    public static final int RETRIES_COUNT = 3;

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(TOPIC_EXCHANGE_NAME);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
        rabbitTemplate.setMandatory(true);
        // 메시지가 브로커에 도착했지만 지정된 큐로 라우팅되지 못한 경우
        rabbitTemplate.setReturnsCallback((returnedMessage) -> {
            log.warn(
                    "Failed Publish - routingKey: {}, replyText: {}",
                    returnedMessage.getRoutingKey(),
                    returnedMessage.getReplyText());
            rabbitTemplate.send(DLQ_EXCHANGE_NAME, returnedMessage.getRoutingKey(), returnedMessage.getMessage());
        });
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
