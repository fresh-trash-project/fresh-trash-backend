package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional(propagation = Propagation.SUPPORTS)
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Page<Alarm> findAllByMember_Id(Long memberId, Pageable pageable);

    @Query(nativeQuery = true, value = "update alarms a set a.read_at = current_timestamp where a.id = ?1 and a.read_at is null")
    void updateReadAtById(Long alarmId);

    boolean existsByIdAndMember_Id(Long alarmId, Long memberId);

    void deleteAllInBatchByReadAtNotNullAndCreatedAtBefore(LocalDateTime localDateTime);
}
