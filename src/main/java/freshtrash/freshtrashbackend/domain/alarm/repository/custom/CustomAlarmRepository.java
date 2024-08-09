package freshtrash.freshtrashbackend.domain.alarm.repository.custom;

import freshtrash.freshtrashbackend.domain.alarm.entity.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomAlarmRepository {

    Page<Alarm> findAllByMember_Id(Long memberId, Boolean isRead, Pageable pageable);
}
