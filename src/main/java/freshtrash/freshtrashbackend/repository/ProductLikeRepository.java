package freshtrash.freshtrashbackend.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EnumExpression;
import freshtrash.freshtrashbackend.entity.ProductLike;
import freshtrash.freshtrashbackend.entity.QProductLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.SUPPORTS)
public interface ProductLikeRepository
        extends JpaRepository<ProductLike, Long>,
                QuerydslPredicateExecutor<ProductLike>,
                QuerydslBinderCustomizer<QProductLike> {

    @Override
    default void customize(QuerydslBindings bindings, QProductLike root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.product.productCategory);
        bindings.bind(root.product.productCategory).as("category").first(EnumExpression::eq);
    }

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);

    void deleteByMemberIdAndProductId(Long memberId, Long productId);

    @EntityGraph(attributePaths = {"product", "product.member"})
    Page<ProductLike> findAll(Predicate predicate, Pageable pageable);
}
