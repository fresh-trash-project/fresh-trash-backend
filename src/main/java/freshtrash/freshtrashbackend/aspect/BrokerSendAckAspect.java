package freshtrash.freshtrashbackend.aspect;

import com.rabbitmq.client.Channel;
import freshtrash.freshtrashbackend.exception.AlarmException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class BrokerSendAckAspect {

    @Pointcut("@annotation(freshtrash.freshtrashbackend.aspect.annotation.ManualAcknowledge)")
    private void publishMessage() {}

    @AfterReturning("publishMessage()")
    public void sendAck(JoinPoint joinpoint) {
        try {
            Object[] args = joinpoint.getArgs();
            Channel channel = (Channel) args[0];
            long tag = (long) args[1];
            channel.basicAck(tag, false);
            log.debug(
                    "Successfully send ack after \"{}\" method",
                    joinpoint.getSignature().getName());
        } catch (Exception e) {
            throw new AlarmException(ErrorCode.FAILED_SEND_ACK_TO_BROKER);
        }
    }
}
