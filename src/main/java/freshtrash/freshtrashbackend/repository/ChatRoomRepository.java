package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(propagation = Propagation.SUPPORTS)
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByWaste_IdAndSellStatusNot(Long wasteId, SellStatus sellStatus);

    @EntityGraph(attributePaths = {"waste", "buyer", "seller", "chatMessages"})
    Optional<ChatRoom> findById(Long chatRoomId);

    @EntityGraph(attributePaths = {"waste", "buyer", "seller"})
    @Query("select cr from ChatRoom cr where cr.buyerId = ?1 or cr.sellerId = ?1 and (cr.openOrClose = true)")
    Page<ChatRoom> findAllBySeller_IdOrBuyer_Id(Long memberId, Pageable pageable);

    @Query("select (cr is not null) from ChatRoom cr where cr.id = ?1 and (cr.buyerId = ?2 or cr.sellerId = ?2)")
    boolean existsByIdAndMemberId(Long chatRoomId, Long memberId);

    @EntityGraph(attributePaths = "waste")
    Optional<ChatRoom> findBySellerIdAndBuyerIdAndWasteId(Long sellerId, Long buyerId, Long wasteId);

    @Modifying
    @Query("update ChatRoom cr set cr.sellStatus = ?2 where cr.id = ?1")
    void updateSellStatus(Long chatRoomId, SellStatus sellStatus);

    @Query(nativeQuery = true, value = "update fresh_trash.chat_rooms cr set cr.open_or_close = false where cr.id = ?1")
    void updateOpenOrClose(Long chatRoomId);
}
