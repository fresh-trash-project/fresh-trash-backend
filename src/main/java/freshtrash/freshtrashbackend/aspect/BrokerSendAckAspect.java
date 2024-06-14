package freshtrash.freshtrashbackend.aspect;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Aspect
@Component
public class BrokerSendAckAspect {

    @Pointcut("@annotation(freshtrash.freshtrashbackend.aspect.annotation.ManualAcknowledge)")
    private void publishMessage() {}

    @Around("publishMessage()")
    public Object sendAck(ProceedingJoinPoint pjp) throws IOException {
        try {
            Object proceedResult = pjp.proceed();
            channelSend(pjp, true);
            return proceedResult;
        } catch (Throwable e) {
            log.warn("occurs error during publish message.", e);
            channelSend(pjp, false);
        }
        return null;
    }

    private void channelSend(ProceedingJoinPoint pjp, boolean ack) throws IOException {
        Object[] args = pjp.getArgs();
        if (args.length >= 2) {
            Channel channel = (Channel) args[0];
            long tag = (long) args[1];
            if (!ack) {
                channel.basicReject(tag, false);
                log.warn(
                        "Successfully send reject after \"{}\" method",
                        pjp.getSignature().getName());
            } else {
                channel.basicAck(tag, false);
            }
        }
    }
}
