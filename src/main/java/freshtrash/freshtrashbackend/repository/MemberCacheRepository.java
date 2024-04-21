package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import org.springframework.data.repository.CrudRepository;

public interface MemberCacheRepository extends CrudRepository<MemberPrincipal, Long> {}
