package freshtrash.freshtrashbackend.dto.response;

import freshtrash.freshtrashbackend.entity.ChatRoom;

public record ChatRoomDetailsResponse(ChatRoomResponse chatRoomResponse, String websocketTopicPath) {

    public static ChatRoomDetailsResponse fromEntity(ChatRoom chatRoom, String websocketTopicPath) {
        return new ChatRoomDetailsResponse(ChatRoomResponse.fromEntity(chatRoom), websocketTopicPath);
    }
}
