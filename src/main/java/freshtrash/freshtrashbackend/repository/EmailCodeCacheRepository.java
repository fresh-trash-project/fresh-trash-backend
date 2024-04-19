package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.dto.cache.EmailCodeCache;
import org.springframework.data.repository.CrudRepository;

public interface EmailCodeCacheRepository extends CrudRepository<EmailCodeCache, String> {}
