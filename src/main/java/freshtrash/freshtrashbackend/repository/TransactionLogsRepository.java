package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionLogsRepository extends JpaRepository<TransactionLog, Long> {}
