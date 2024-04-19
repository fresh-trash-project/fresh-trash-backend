package freshtrash.freshtrashbackend.dto.response;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatRoomResponse(
        Long id,
        SellStatus sellStatus,
        boolean openOrClose,
        LocalDateTime createdAt,
        String sellerNickname,
        String buyerNickname) {

    public static ChatRoomResponse fromEntity(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .sellStatus(chatRoom.getSellStatus())
                .openOrClose(chatRoom.isOpenOrClose())
                .createdAt(chatRoom.getCreatedAt())
                .sellerNickname(chatRoom.getSeller().getNickname())
                .buyerNickname(chatRoom.getBuyer().getNickname())
                .build();
    }

    public static ChatRoomResponse fromEntity(ChatRoom chatRoom, String sellerNickname, String buyerNickname) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .sellStatus(chatRoom.getSellStatus())
                .openOrClose(chatRoom.isOpenOrClose())
                .createdAt(chatRoom.getCreatedAt())
                .sellerNickname(sellerNickname)
                .buyerNickname(buyerNickname)
                .build();
    }
}