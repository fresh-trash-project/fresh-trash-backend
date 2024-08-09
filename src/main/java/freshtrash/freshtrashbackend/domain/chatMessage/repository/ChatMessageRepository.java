package freshtrash.freshtrashbackend.domain.chatMessage.repository;

import freshtrash.freshtrashbackend.domain.chatMessage.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.SUPPORTS)
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {}
