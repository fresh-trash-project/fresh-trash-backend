package freshtrash.freshtrashbackend.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class EmitterRepository {
    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    public SseEmitter save(Long memberId, SseEmitter sseEmitter) {
        String key = getKey(memberId);
        emitterMap.put(key, sseEmitter);
        return sseEmitter;
    }

    public Optional<SseEmitter> get(Long memberId) {
        String key = getKey(memberId);
        return Optional.ofNullable(emitterMap.get(key));
    }

    public void delete(Long memberId) {
        emitterMap.remove(getKey(memberId));
    }

    private String getKey(Long memberId) {
        return "Emitter:Id:" + memberId;
    }
}
