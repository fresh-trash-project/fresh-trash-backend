package freshtrash.freshtrashbackend.dto.request;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import lombok.Builder;

@Builder
public record AlarmPayload(String message, Long wasteId, Long memberId, Long fromMemberId, AlarmType alarmType) {
    public static AlarmPayload ofProductDealByBuyer(String message, ChatRoom chatRoom) {
        return ofProductDeal(message, chatRoom)
                .memberId(chatRoom.getSellerId())
                .fromMemberId(chatRoom.getBuyerId())
                .build();
    }

    public static AlarmPayload ofProductDealBySeller(String message, ChatRoom chatRoom) {
        return ofProductDeal(message, chatRoom)
                .memberId(chatRoom.getBuyerId())
                .fromMemberId(chatRoom.getSellerId())
                .build();
    }

    private static AlarmPayloadBuilder ofProductDeal(String message, ChatRoom chatRoom) {
        return AlarmPayload.builder()
                .message(message)
                .wasteId(chatRoom.getWasteId())
                .alarmType(AlarmType.TRANSACTION);
    }

    public static AlarmPayload ofUserFlag(String message, Long wasteId, Long targetMemberId, Long currentMemberId) {
        return AlarmPayload.builder()
                .message(message)
                .wasteId(wasteId)
                .memberId(targetMemberId)
                .fromMemberId(currentMemberId)
                .alarmType(AlarmType.FLAG)
                .build();
    }
}
