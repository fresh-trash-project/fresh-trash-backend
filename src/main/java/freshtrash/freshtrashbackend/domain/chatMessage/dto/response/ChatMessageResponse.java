package freshtrash.freshtrashbackend.domain.chatMessage.dto.response;

import freshtrash.freshtrashbackend.domain.chatMessage.entity.ChatMessage;

import java.time.LocalDateTime;

public record ChatMessageResponse(String message, Long sentMemberId, String sentMemberNickname, LocalDateTime createdAt) {
    public static ChatMessageResponse fromEntity(ChatMessage chatMessage) {
        return new ChatMessageResponse(chatMessage.getMessage(), chatMessage.getMember().getId(), chatMessage.getMember().getNickname(), chatMessage.getCreatedAt());
    }
}
