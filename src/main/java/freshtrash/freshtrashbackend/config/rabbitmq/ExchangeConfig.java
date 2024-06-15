package freshtrash.freshtrashbackend.config.rabbitmq;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static freshtrash.freshtrashbackend.config.rabbitmq.RabbitMQConfig.*;

@Configuration
public class ExchangeConfig {
    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }

    @Bean
    TopicExchange dlqExchange() {
        return new TopicExchange(DLQ_EXCHANGE_NAME);
    }

    @Bean
    TopicExchange parkingLotExchange() {
        return new TopicExchange(PARKING_LOT_EXCHANGE_NAME);
    }
}
