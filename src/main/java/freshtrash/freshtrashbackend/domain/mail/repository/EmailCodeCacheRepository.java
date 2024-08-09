package freshtrash.freshtrashbackend.domain.mail.repository;

import freshtrash.freshtrashbackend.domain.mail.dto.cache.EmailCodeCache;
import org.springframework.data.repository.CrudRepository;

public interface EmailCodeCacheRepository extends CrudRepository<EmailCodeCache, String> {}
