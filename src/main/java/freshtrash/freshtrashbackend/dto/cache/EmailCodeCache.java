package freshtrash.freshtrashbackend.dto.cache;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "emailCode")
public record EmailCodeCache(@Id String email, String code, @TimeToLive Long expiration) {
    public static EmailCodeCache of(String email, String code, Long expiration) {
        return new EmailCodeCache(email, code, expiration);
    }
}
