package freshtrash.freshtrashbackend.dto.response;

import freshtrash.freshtrashbackend.entity.ChatRoom;

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
