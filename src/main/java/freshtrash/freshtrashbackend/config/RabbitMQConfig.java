package freshtrash.freshtrashbackend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String WASTE_ID_KEY = "wasteId";
    public static final String MEMBER_ID_KEY = "memberId";
    public static final String FROM_MEMBER_ID_KEY = "fromMemberId";
    public static final String WASTE_TRANSACTION_ROUTING_KEY = "waste.transaction";
    public static final String ALARM_TYPE = "alarmType";
    private static final String directExchangeName = "waste-direct-exchange";

    private static final String WASTE_QUEUE = "waste-queue";

    @Bean
    Queue wasteQueue() {
        return new Queue(WASTE_QUEUE, false);
    }

    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(directExchangeName);
    }

    @Bean
    Binding directBinding(Queue queue, DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(WASTE_TRANSACTION_ROUTING_KEY);
    }
}
