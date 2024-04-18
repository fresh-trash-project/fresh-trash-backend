package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.SUPPORTS)
public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {}
