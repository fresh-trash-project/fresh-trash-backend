package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {}
