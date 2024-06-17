package freshtrash.freshtrashbackend.dto.request;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductAlarmPayload extends BaseAlarmPayload {

    @Override
    public Map<String, String> toMap() {
        Map<String, String> data = new HashMap<>();
        data.put("productId", targetId.toString());
        data.put("memberId", memberId.toString());
        data.put("fromMemberId", fromMemberId.toString());
        data.put("alarmType", alarmType.name());
        return data;
    }

    /**
     * 구매자에게 리뷰 요청 알림
     */
    public static BaseAlarmPayload ofRequestReview(String message, ChatRoom chatRoom, AlarmType alarmType) {
        return ofProductDeal(message, chatRoom, alarmType)
                .memberId(chatRoom.getSellerId())
                .fromMemberId(chatRoom.getBuyerId())
                .build();
    }

    /**
     * 판매자가 올린 상품이 거래되었음을 채팅 요청한 구매자들 또는 판매자에게 알림
     */
    public static BaseAlarmPayload ofCompletedProductDeal(String message, ChatRoom chatRoom, AlarmType alarmType) {
        return ofProductDeal(message, chatRoom, alarmType)
                .memberId(chatRoom.getBuyerId())
                .fromMemberId(chatRoom.getSellerId())
                .build();
    }

    /**
     * 예약을 취소하거나 요청할 때 상품의 판매 상태가 변경되었음을 알림
     */
    public static BaseAlarmPayload ofUpdatedSellStatus(String message, ChatRoom chatRoom, AlarmType alarmType) {
        return ofProductDeal(message, chatRoom, alarmType)
                .memberId(chatRoom.getBuyerId())
                .fromMemberId(chatRoom.getSellerId())
                .build();
    }

    /**
     * 현재 사용자에의해 특정 사용자가 신고되었음을 알림
     */
    public static BaseAlarmPayload ofUserFlag(
            String message, Long productId, Long targetMemberId, Long currentMemberId) {
        return ProductAlarmPayload.builder()
                .message(message)
                .targetId(productId)
                .memberId(targetMemberId)
                .fromMemberId(currentMemberId)
                .alarmType(AlarmType.FLAG)
                .build();
    }

    private static ProductAlarmPayloadBuilder ofProductDeal(String message, ChatRoom chatRoom, AlarmType alarmType) {
        return ProductAlarmPayload.builder()
                .message(message)
                .targetId(chatRoom.getProductId())
                .alarmType(alarmType);
    }

    @Builder
    private ProductAlarmPayload(String message, Long targetId, Long memberId, Long fromMemberId, AlarmType alarmType) {
        this.message = message;
        this.targetId = targetId;
        this.memberId = memberId;
        this.fromMemberId = fromMemberId;
        this.alarmType = alarmType;
    }
}
