package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.request.MessageRequest;
import freshtrash.freshtrashbackend.dto.response.AlarmResponse;
import freshtrash.freshtrashbackend.entity.Alarm;
import freshtrash.freshtrashbackend.exception.AlarmException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.AlarmRepository;
import freshtrash.freshtrashbackend.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
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
     * - 읽지 않은 알람(readAt == null)만 조회
     */
    public Page<AlarmResponse> getAlarms(Long memberId, Pageable pageable) {
        return alarmRepository
                .findAllByMember_IdAndReadAtIsNull(memberId, pageable)
                .map(AlarmResponse::fromEntity);
    }

    /**
     * 알람 메시지 전송 Listener
     */
    @RabbitListener(queues = {"#{wasteCompleteQueue.name}", "#{wasteFlagQueue.name}", "#{wasteChangeStatusQueue.name}"})
    public void receiveWasteTransaction(@Payload MessageRequest messageRequest) {
        log.debug("receive complete transaction message: {}", messageRequest);
        Alarm alarm = saveAlarm(messageRequest);
        receive(messageRequest.memberId(), AlarmResponse.fromEntity(alarm));
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

    public boolean isOwnerOfAlarm(Long alarmId, Long memberId) {
        return alarmRepository.existsByIdAndMember_Id(alarmId, memberId);
    }

    private Alarm saveAlarm(MessageRequest messageRequest) {
        return alarmRepository.save(Alarm.fromMessageRequest(messageRequest));
    }
}
