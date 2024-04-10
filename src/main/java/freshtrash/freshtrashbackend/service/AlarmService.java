package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.exception.AlarmException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.AlarmRepository;
import freshtrash.freshtrashbackend.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {
    private static final Long SSE_TIMEOUT = TimeUnit.MINUTES.toMillis(30);
    private final EmitterRepository emitterRepository;
    private final AlarmRepository alarmRepository;

    public SseEmitter connectAlarm(Long memberId) {
        SseEmitter sseEmitter = new SseEmitter(SSE_TIMEOUT);
        emitterRepository.save(memberId, sseEmitter);
        sseEmitter.onCompletion(() -> emitterRepository.delete(memberId));
        sseEmitter.onTimeout(() -> emitterRepository.delete(memberId));
        sseEmitter.onError((e) -> {
            emitterRepository.delete(memberId);
            log.error("SseEmitter Error: ", e);
        });

        try {
            // 처음 SSE 연결 후 아무런 이벤트도 보내지 않으면 재연결 요청을 보낼때나 연결 요청 자체에서 에러가 발생합니다. 따라서 임의로 데이터를 전송합니다.
            sseEmitter.send(SseEmitter.event().id("").name("connect").data("connect completed"));
        } catch (IOException e) {
            throw new AlarmException(ErrorCode.ALARM_CONNECT_ERROR, e);
        }

        return sseEmitter;
    }
}
