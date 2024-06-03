package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.ProductDealLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.SUPPORTS)
public interface ProductDealLogRepository extends JpaRepository<ProductDealLog, Long> {
    @EntityGraph(attributePaths = {"product", "product.member"})
    Page<ProductDealLog> findAllBySeller_Id(Long memberId, Pageable pageable);

    @EntityGraph(attributePaths = {"product", "product.member"})
    Page<ProductDealLog> findAllByBuyer_Id(Long memberId, Pageable pageable);
}
