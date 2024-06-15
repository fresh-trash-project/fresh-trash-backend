package freshtrash.freshtrashbackend.dto.request;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

@Builder
public record AlarmPayload(String message, Long productId, Long memberId, Long fromMemberId, AlarmType alarmType) {
    public Map<String, String> toMap() {
        Map<String, String> data = new HashMap<>();
        data.put("productId", productId.toString());
        data.put("memberId", memberId.toString());
        data.put("fromMemberId", fromMemberId.toString());
        data.put("alarmType", alarmType.name());
        return data;
    }

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

    public static AlarmPayload ofUserFlag(String message, Long productId, Long targetMemberId, Long currentMemberId) {
        return AlarmPayload.builder()
                .message(message)
                .productId(productId)
                .memberId(targetMemberId)
                .fromMemberId(currentMemberId)
                .alarmType(AlarmType.FLAG)
                .build();
    }

    private static AlarmPayloadBuilder ofProductDeal(String message, ChatRoom chatRoom, AlarmType alarmType) {
        return AlarmPayload.builder()
                .message(message)
                .productId(chatRoom.getProductId())
                .alarmType(alarmType);
    }
}
