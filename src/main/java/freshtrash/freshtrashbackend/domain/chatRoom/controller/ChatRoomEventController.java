package freshtrash.freshtrashbackend.domain.chatRoom.controller;

import freshtrash.freshtrashbackend.domain.alarm.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.domain.alarm.service.adapter.AlarmMappingHandlerAdapter;
import freshtrash.freshtrashbackend.domain.chatRoom.controller.constants.ProductEventType;
import freshtrash.freshtrashbackend.domain.chatRoom.entity.ChatRoom;
import freshtrash.freshtrashbackend.domain.chatRoom.service.ChatRoomService;
import freshtrash.freshtrashbackend.domain.member.dto.security.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatRoomEventController {
    private final ChatRoomService chatRoomService;
    private final AlarmMappingHandlerAdapter alarmMappingHandlerAdapter;

    /**
     * 신고하기(채팅 상대방)
     */
    @PostMapping("/{chatRoomId}/flag")
    public ResponseEntity<Void> flagMember(
            @PathVariable Long chatRoomId, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId, memberPrincipal.id());
        log.debug("채팅방 {} 조회...", chatRoomId);
        alarmMappingHandlerAdapter.handle(chatRoom, memberPrincipal.id(), AlarmType.FLAG);
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
        alarmMappingHandlerAdapter.handle(chatRoomId, memberPrincipal.id(), productEventType.getAlarmType());
        return ResponseEntity.ok(null);
    }
}
