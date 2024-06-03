package freshtrash.freshtrashbackend.repository;

import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.StringPath;
import freshtrash.freshtrashbackend.entity.Product;
import freshtrash.freshtrashbackend.entity.QProduct;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.repository.custom.CustomProductRepository;
import freshtrash.freshtrashbackend.repository.projections.FileNameSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(propagation = Propagation.SUPPORTS)
public interface ProductRepository
        extends JpaRepository<Product, Long>, CustomProductRepository, QuerydslBinderCustomizer<QProduct> {
    @Override
    default void customize(QuerydslBindings bindings, QProduct root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.title, root.productCategory);
        bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
        bindings.bind(root.productCategory).as("category").first(EnumExpression::eq);
    }

    @EntityGraph(attributePaths = "member")
    Optional<Product> findById(Long productId);

    Optional<FileNameSummary> findFileNameById(Long productId);

    boolean existsByIdAndMember_Id(Long productId, Long memberId);

    @Modifying
    @Query("update Product w set w.likeCount = w.likeCount + ?2 where w.id = ?1")
    void updateLikeCount(Long productId, int updateCount);

    @Modifying
    @Query("update Product w set w.sellStatus = ?2 where w.id = ?1")
    void updateSellStatus(Long productId, SellStatus sellStatus);

    @Query(nativeQuery = true, value = "update products w set w.view_count = w.view_count + 1 where w.id = ?1")
    void updateViewCount(Long productId);

    @EntityGraph(attributePaths = "member")
    Page<Product> findAllByMemberIdAndSellStatusNot(Long memberId, SellStatus sellStatus, Pageable pageable);
}
