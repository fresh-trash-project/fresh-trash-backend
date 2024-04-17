package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(propagation = Propagation.SUPPORTS)
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByWaste_Id(Long wasteId);

    @EntityGraph(attributePaths = {"buyer", "seller"})
    Page<ChatRoom> findAllBySeller_IdOrBuyer_Id(Long memberId, Pageable pageable);

    @Query("select cr from ChatRoom cr where cr.id = ?1 and (cr.buyerId = ?2 or cr.sellerId = ?2)")
    boolean existsByIdAndSeller_IdOrBuyer_Id(Long chatRoomId, Long memberId);
}
