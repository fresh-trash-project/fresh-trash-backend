package freshtrash.freshtrashbackend.dto.response;

import freshtrash.freshtrashbackend.entity.ChatRoom;

public record ChatRoomDetailsResponse(ChatRoomResponse chatRoomResponse) {

    public static ChatRoomDetailsResponse fromEntity(ChatRoom chatRoom) {
        return new ChatRoomDetailsResponse(ChatRoomResponse.fromEntity(chatRoom));
    }
}
