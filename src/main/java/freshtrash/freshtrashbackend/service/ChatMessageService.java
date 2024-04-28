package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.entity.ChatMessage;
import freshtrash.freshtrashbackend.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage saveChatMessage(Long chatRoomId, Long memberId, String message) {
        return chatMessageRepository.save(ChatMessage.of(chatRoomId, memberId, message));
    }
}
