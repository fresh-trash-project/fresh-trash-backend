package freshtrash.freshtrashbackend.dto.cache;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "EmailCode", timeToLive = 10 * 60)
public record EmailCodeCache(@Id String email, String code) {
    public static EmailCodeCache of(String email, String code) {
        return new EmailCodeCache(email, code);
    }
}
