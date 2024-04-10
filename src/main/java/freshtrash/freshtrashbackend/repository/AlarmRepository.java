package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {}
