package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.SUPPORTS)
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    boolean existsByProductId(Long productId);
}
