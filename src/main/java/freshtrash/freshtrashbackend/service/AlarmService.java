package freshtrash.freshtrashbackend.service;

import com.rabbitmq.client.Channel;
import freshtrash.freshtrashbackend.aspect.annotation.ManualAcknowledge;
import freshtrash.freshtrashbackend.dto.request.AlarmPayload;
import freshtrash.freshtrashbackend.dto.response.AlarmResponse;
import freshtrash.freshtrashbackend.entity.Alarm;
import freshtrash.freshtrashbackend.exception.AlarmException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.AlarmRepository;
import freshtrash.freshtrashbackend.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {
    private static final Long SSE_TIMEOUT = TimeUnit.MINUTES.toMillis(30);
    private static final String CONNECTED_ALARM_NAME = "connected";
    private static final String WASTE_TRANSACTION_ALARM_NAME = "waste-transaction-alarm";
    private final EmitterRepository emitterRepository;
    private final AlarmRepository alarmRepository;

    /**
     * 전체 알람 조회
     * - 읽지 않은 알람도 같이 조회
     */
    public Page<AlarmResponse> getAlarms(Long memberId, Pageable pageable) {
        return alarmRepository.findAllByMember_Id(memberId, pageable).map(AlarmResponse::fromEntity);
    }

    /**
     * 알람 메시지 전송 Listener
     */
    @ManualAcknowledge
    @RabbitListener(queues = {"#{wasteCompleteQueue.name}", "#{wasteFlagQueue.name}", "#{wasteChangeStatusQueue.name}"})
    public void receiveWasteTransaction(
            Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag, @Payload AlarmPayload alarmPayload) {
        log.debug("receive complete transaction message: {}", alarmPayload);
        Alarm alarm = saveAlarm(alarmPayload);
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
                                        .name(WASTE_TRANSACTION_ALARM_NAME)
                                        .data(alarmResponse));
                            } catch (IOException e) {
                                emitterRepository.deleteByMemberId(memberId);
                                throw new AlarmException(ErrorCode.ALARM_CONNECT_ERROR, e);
                            }
                        },
                        () -> log.error("Emiter를 찾을 수 없습니다."));
    }

    /**
     * SSE 연결 요청
     */
    public SseEmitter connectAlarm(Long memberId) {
        log.debug("connect alarm");
        SseEmitter sseEmitter = new SseEmitter(SSE_TIMEOUT);
        emitterRepository.save(memberId, sseEmitter);
        sseEmitter.onCompletion(() -> {
            emitterRepository.deleteByMemberId(memberId);
            log.debug("SseEmitter Complete!");
        });
        sseEmitter.onTimeout(() -> {
            emitterRepository.deleteByMemberId(memberId);
            log.debug("SseEmitter Timeout!");
        });
        sseEmitter.onError((e) -> {
            emitterRepository.deleteByMemberId(memberId);
            log.error("SseEmitter Error: ", e);
        });

        try {
            // 처음 SSE 연결 후 아무런 이벤트도 보내지 않으면 재연결 요청을 보낼때나 연결 요청 자체에서 에러가 발생합니다. 따라서 임의로 데이터를 전송합니다.
            log.debug("send connected alarm");
            sseEmitter.send(SseEmitter.event().id("").name(CONNECTED_ALARM_NAME).data("connect completed"));
        } catch (IOException e) {
            throw new AlarmException(ErrorCode.ALARM_CONNECT_ERROR, e);
        }

        return sseEmitter;
    }

    public void readAlarm(Long alarmId) {
        alarmRepository.updateReadAtById(alarmId);
    }

    /**
     * 1개월이 지난 알람 모두 삭제
     * - 매달 0시에 수행
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteAlarms() {
        log.debug("delete alarms!!");
        alarmRepository.deleteAllInBatchByReadAtNotNullAndCreatedAtBefore(
                LocalDateTime.now().minusMonths(1));
        log.debug("successfully deleted alarms!!");
    }

    public boolean isOwnerOfAlarm(Long alarmId, Long memberId) {
        return alarmRepository.existsByIdAndMember_Id(alarmId, memberId);
    }

    private Alarm saveAlarm(AlarmPayload alarmPayload) {
        return alarmRepository.save(Alarm.fromMessageRequest(alarmPayload));
    }
}
