package freshtrash.freshtrashbackend.repository;

import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.StringPath;
import freshtrash.freshtrashbackend.dto.response.WasteResponse;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.entity.QWaste;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.repository.custom.CustomWasteRepository;
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
public interface WasteRepository
        extends JpaRepository<Waste, Long>, CustomWasteRepository, QuerydslBinderCustomizer<QWaste> {
    @Override
    default void customize(QuerydslBindings bindings, QWaste root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.title, root.wasteCategory);
        bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
        bindings.bind(root.wasteCategory).as("category").first(EnumExpression::eq);
    }

    @EntityGraph(attributePaths = "member")
    Optional<Waste> findById(Long wasteId);

    Optional<FileNameSummary> findFileNameById(Long wasteId);

    boolean existsByIdAndMember_Id(Long wasteId, Long memberId);

    @Modifying
    @Query("update Waste w set w.likeCount = w.likeCount + ?2 where w.id = ?1")
    void updateLikeCount(Long wasteId, int updateCount);

    @Modifying
    @Query("update Waste w set w.sellStatus = ?2 where w.id = ?1")
    void updateSellStatus(Long wasteId, SellStatus sellStatus);

    @Query(nativeQuery = true, value = "update wastes w set w.view_count = w.view_count + 1 where w.id = ?1")
    void updateViewCount(Long wasteId);

    @EntityGraph(attributePaths = "member")
    Page<Waste> findAllByMemberIdAndSellStatusNot(Long memberId, SellStatus sellStatus, Pageable pageable);
}
