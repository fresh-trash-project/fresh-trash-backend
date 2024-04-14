package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.TransactionLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionLogsRepository extends JpaRepository<TransactionLogs, Long> {}
