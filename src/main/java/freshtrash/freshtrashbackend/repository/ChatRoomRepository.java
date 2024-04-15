package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByWaste_Id(Long wasteId);
}
