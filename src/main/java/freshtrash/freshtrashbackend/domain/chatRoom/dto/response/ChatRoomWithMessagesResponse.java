package freshtrash.freshtrashbackend.domain.chatRoom.dto.response;

import freshtrash.freshtrashbackend.domain.chatMessage.dto.response.ChatMessageResponse;
import freshtrash.freshtrashbackend.domain.chatRoom.entity.ChatRoom;

import java.util.List;

public record ChatRoomWithMessagesResponse(ChatRoomResponse chatRoom, List<ChatMessageResponse> messages) {

    public static ChatRoomWithMessagesResponse fromEntity(ChatRoom chatRoom) {
        return new ChatRoomWithMessagesResponse(
                ChatRoomResponse.fromEntity(chatRoom),
                chatRoom.getChatMessages().stream()
                        .map(ChatMessageResponse::fromEntity)
                        .toList());
    }
}
