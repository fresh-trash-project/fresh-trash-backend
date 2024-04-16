package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Page<Alarm> findAllByMember_IdAndReadAtIsNull(Long memberId, Pageable pageable);

    @Query(nativeQuery = true, value = "update alarms a set a.read_at = now() where a.id = ?1")
    void updateReadAtById(Long alarmId);


    boolean existsByIdAndMember_Id(Long alarmId, Long memberId);
}
