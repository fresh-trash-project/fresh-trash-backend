package freshtrash.freshtrashbackend.dto.response;

import freshtrash.freshtrashbackend.entity.ChatMessage;

import java.time.LocalDateTime;

public record ChatMessageResponse(String message, LocalDateTime createdAt) {
    public static ChatMessageResponse fromEntity(ChatMessage chatMessage) {
        return new ChatMessageResponse(chatMessage.getMessage(), chatMessage.getCreatedAt());
    }
}
