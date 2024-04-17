package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Page<Alarm> findAllByMember_IdAndReadAtIsNull(Long memberId, Pageable pageable);
}
