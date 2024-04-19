package freshtrash.freshtrashbackend.dto.response;

import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.Waste;

public record ChatRoomDetailsResponse(ChatRoomResponse chatRoomResponse, String websocketTopicPath) {

    public static ChatRoomDetailsResponse fromEntity(ChatRoom chatRoom, String sellerNickname, String buyerNickname, String websocketTopicPath) {
        return new ChatRoomDetailsResponse(ChatRoomResponse.fromEntity(chatRoom, sellerNickname, buyerNickname), websocketTopicPath);
    }
}
