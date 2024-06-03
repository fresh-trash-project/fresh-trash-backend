package freshtrash.freshtrashbackend.dto.request;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import lombok.Builder;

@Builder
public record AlarmPayload(String message, Long wasteId, Long memberId, Long fromMemberId, AlarmType alarmType) {
    public static AlarmPayload ofProductDealByBuyer(String message, ChatRoom chatRoom, AlarmType alarmType) {
        return ofProductDeal(message, chatRoom, alarmType)
                .memberId(chatRoom.getSellerId())
                .fromMemberId(chatRoom.getBuyerId())
                .build();
    }

    public static AlarmPayload ofProductDealBySeller(String message, ChatRoom chatRoom, AlarmType alarmType) {
        return ofProductDeal(message, chatRoom, alarmType)
                .memberId(chatRoom.getBuyerId())
                .fromMemberId(chatRoom.getSellerId())
                .build();
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

    private static AlarmPayloadBuilder ofProductDeal(String message, ChatRoom chatRoom, AlarmType alarmType) {
        return AlarmPayload.builder()
                .message(message)
                .wasteId(chatRoom.getWasteId())
                .alarmType(alarmType);
    }
}
