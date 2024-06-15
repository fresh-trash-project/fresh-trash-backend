package freshtrash.freshtrashbackend.consumer;

import com.rabbitmq.client.Channel;
import freshtrash.freshtrashbackend.aspect.annotation.ManualAcknowledge;
import freshtrash.freshtrashbackend.dto.request.AlarmPayload;
import freshtrash.freshtrashbackend.dto.response.AlarmResponse;
import freshtrash.freshtrashbackend.entity.Alarm;
import freshtrash.freshtrashbackend.exception.AlarmException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.EmitterRepository;
import freshtrash.freshtrashbackend.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductAlarmConsumer {
    private final EmitterRepository emitterRepository;
    private final AlarmService alarmService;

    /**
     * 알람 메시지 전송 Listener
     */
    @ManualAcknowledge
    @RabbitListener(
            queues = {"#{productCompleteQueue.name}", "#{productFlagQueue.name}", "#{productChangeStatusQueue.name}"})
    public void receiveProductDeal(
            Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag, @Payload AlarmPayload alarmPayload) {
        log.debug("receive complete productDeal message: {}", alarmPayload);
        Alarm alarm = alarmService.saveAlarm(alarmPayload);
        receive(alarmPayload.memberId(), AlarmResponse.fromEntity(alarm));
    }

    /**
     * SSE 알람 전송
     * @param memberId 알람을 받는 사용자 id
     */
    private void receive(Long memberId, AlarmResponse alarmResponse) {
        emitterRepository
                .findByMemberId(memberId)
                .ifPresentOrElse(
                        sseEmitter -> {
                            try {
                                sseEmitter.send(SseEmitter.event()
                                        .id(String.valueOf(alarmResponse.id()))
                                        .name(alarmResponse.alarmType().name())
                                        .data(alarmResponse));
                            } catch (IOException e) {
                                emitterRepository.deleteByMemberId(memberId);
                                throw new AlarmException(ErrorCode.ALARM_CONNECT_ERROR, e);
                            }
                        },
                        () -> log.error("Emiter를 찾을 수 없습니다."));
    }
}
