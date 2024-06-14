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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId, memberPrincipal.id());
        log.debug("채팅방 {} 조회...", chatRoomId);
        userFlagChatAlarm.sendAlarm(chatRoom, memberPrincipal.id());
        return ResponseEntity.ok(null);
    }

    /**
     * 거래 처리 (판매 중, 예약 중, 판매 완료)
     */
    @PostMapping("/{chatRoomId}/productDeal")
    public ResponseEntity<Void> handleProductDeal(
            @PathVariable Long chatRoomId,
            @RequestParam ProductEventType productEventType,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        switch (productEventType) {
            case CANCEL_BOOKING -> cancelBookingProductAlarm.sendAlarm(chatRoomId, memberPrincipal.id());
            case REQUEST_BOOKING -> requestBookingProductAlarm.sendAlarm(chatRoomId, memberPrincipal.id());
            default -> completeDealProductAlarm.sendAlarm(chatRoomId, memberPrincipal.id());
        }
        return ResponseEntity.ok(null);
    }
}
