package freshtrash.freshtrashbackend.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EnumExpression;
import freshtrash.freshtrashbackend.entity.QWasteLike;
import freshtrash.freshtrashbackend.entity.WasteLike;
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
public interface WasteLikeRepository
        extends JpaRepository<WasteLike, Long>,
                QuerydslPredicateExecutor<WasteLike>,
                QuerydslBinderCustomizer<QWasteLike> {

    @Override
    default void customize(QuerydslBindings bindings, QWasteLike root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.waste.wasteCategory);
        bindings.bind(root.waste.wasteCategory).as("category").first(EnumExpression::eq);
    }

    boolean existsByMemberIdAndWasteId(Long memberId, Long wasteId);

    void deleteByMemberIdAndWasteId(Long memberId, Long wasteId);

    @EntityGraph(attributePaths = {"waste", "waste.member"})
    Page<WasteLike> findAll(Predicate predicate, Pageable pageable);
}
