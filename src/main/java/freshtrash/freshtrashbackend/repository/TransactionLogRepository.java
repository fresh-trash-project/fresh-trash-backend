package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.TransactionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.SUPPORTS)
public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {
    @EntityGraph(attributePaths = {"waste", "waste.member"})
    Page<TransactionLog> findAllBySeller_Id(Long memberId, Pageable pageable);

    @EntityGraph(attributePaths = {"waste", "waste.member"})
    Page<TransactionLog> findAllByBuyer_Id(Long memberId, Pageable pageable);
}
