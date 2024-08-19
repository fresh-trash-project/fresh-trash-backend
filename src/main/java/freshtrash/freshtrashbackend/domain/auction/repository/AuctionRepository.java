package freshtrash.freshtrashbackend.domain.auction.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.StringPath;
import freshtrash.freshtrashbackend.domain.auction.entity.Auction;
import freshtrash.freshtrashbackend.domain.auction.entity.QAuction;
import freshtrash.freshtrashbackend.domain.auction.entity.constants.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(propagation = Propagation.SUPPORTS)
public interface AuctionRepository
        extends JpaRepository<Auction, Long>, QuerydslBinderCustomizer<QAuction>, QuerydslPredicateExecutor<Auction> {
    @Override
    default void customize(QuerydslBindings bindings, QAuction root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.title, root.productCategory);
        bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
        bindings.bind(root.productCategory).as("category").first(EnumExpression::eq);
    }

    @EntityGraph(attributePaths = "member")
    Page<Auction> findAll(Predicate predicate, Pageable pageable);

    @EntityGraph(attributePaths = "member")
    Page<Auction> findAllByMemberIdAndAuctionStatus(Long memberId, AuctionStatus auctionStatus, Pageable pageable);

    @EntityGraph(attributePaths = "member")
    Optional<Auction> findById(Long auctionId);

    @EntityGraph(attributePaths = {"member", "biddingHistories"})
    Optional<Auction> findWithBiddingHistoryById(Long auctionId);

    boolean existsByIdAndMemberId(Long auctionId, Long memberId);

    @EntityGraph(attributePaths = "biddingHistories")
    @Query("select a from Auction a where a.auctionStatus = 'ONGOING' and a.endedAt < current_timestamp")
    List<Auction> findAllEndedAuctions();

    @Query(nativeQuery = true, value = "update auctions a set a.auction_status = 'CLOSE' where a.id = ?1")
    void closeAuctionById(Long auctionId);

    @Query(nativeQuery = true, value = "update auctions a set a.auction_status = 'CANCEL' where a.id = ?1")
    void cancelAuctionById(Long auctionId);

    @Query(nativeQuery = true, value = "update auctions a set a.view_count = a.view_count + 1 where a.id = ?1")
    void updateViewCount(Long auctionId);
}
