package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.controller.constants.ProductEventType;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.alarm.CancelBookingProductAlarm;
import freshtrash.freshtrashbackend.service.alarm.CompleteDealProductAlarm;
import freshtrash.freshtrashbackend.service.alarm.RequestBookingProductAlarm;
import freshtrash.freshtrashbackend.service.alarm.UserFlagChatAlarm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatRoomEventApi {
    private final ChatRoomService chatRoomService;
    private final UserFlagChatAlarm userFlagChatAlarm;
    private final CancelBookingProductAlarm cancelBookingProductAlarm;
    private final CompleteDealProductAlarm completeDealProductAlarm;
    private final RequestBookingProductAlarm requestBookingProductAlarm;

    /**
     * 신고하기(채팅 상대방)
     */
    @PostMapping("/{chatRoomId}/flag")
    public ResponseEntity<Void> flagMember(
            @PathVariable Long chatRoomId, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId);
        userFlagChatAlarm.sendAlarm(chatRoom, memberPrincipal.id());
        return ResponseEntity.ok(null);
    }

    /**
     * 거래 처리 (판매 중, 예약 중, 판매 완료)
     */
    @PostMapping("/{chatRoomId}/productDeal")
    public ResponseEntity<Void> handleProductDeal(
            @PathVariable Long chatRoomId, @RequestParam ProductEventType productEventType) {
        switch (productEventType) {
            case CANCEL_BOOKING -> cancelBookingProductAlarm.sendAlarm(chatRoomId);
            case REQUEST_BOOKING -> requestBookingProductAlarm.sendAlarm(chatRoomId);
            default -> completeDealProductAlarm.sendAlarm(chatRoomId);
        }
        return ResponseEntity.ok(null);
    }
}
