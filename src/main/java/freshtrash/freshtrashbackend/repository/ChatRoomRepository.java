package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {}
