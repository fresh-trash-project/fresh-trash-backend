package freshtrash.freshtrashbackend.dto.response;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatRoomResponse(
        Long id,
        Long wasteId,
        String wasteTitle,
        SellStatus sellStatus,
        boolean openOrClose,
        LocalDateTime createdAt,
        Long sellerId,
        String sellerNickname,
        Long buyerId,
        String buyerNickname) {

    public static ChatRoomResponse fromEntity(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .wasteId(chatRoom.getWasteId())
                .wasteTitle(chatRoom.getWaste().getTitle())
                .sellStatus(chatRoom.getSellStatus())
                .openOrClose(chatRoom.isOpenOrClose())
                .createdAt(chatRoom.getCreatedAt())
                .sellerId(chatRoom.getSellerId())
                .sellerNickname(chatRoom.getSeller().getNickname())
                .buyerId(chatRoom.getBuyerId())
                .buyerNickname(chatRoom.getBuyer().getNickname())
                .build();
    }

    public static ChatRoomResponse fromEntity(ChatRoom chatRoom, String wasteTitle, String sellerNickname, String buyerNickname) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .wasteId(chatRoom.getWasteId())
                .wasteTitle(wasteTitle)
                .sellStatus(chatRoom.getSellStatus())
                .openOrClose(chatRoom.isOpenOrClose())
                .createdAt(chatRoom.getCreatedAt())
                .sellerId(chatRoom.getSellerId())
                .sellerNickname(sellerNickname)
                .buyerId(chatRoom.getBuyerId())
                .buyerNickname(buyerNickname)
                .build();
    }
}