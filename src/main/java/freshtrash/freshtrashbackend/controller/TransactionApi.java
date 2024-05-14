package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.constants.TransactionMemberType;
import freshtrash.freshtrashbackend.dto.events.AlarmEvent;
import freshtrash.freshtrashbackend.dto.request.MessageRequest;
import freshtrash.freshtrashbackend.dto.response.ApiResponse;
import freshtrash.freshtrashbackend.dto.response.WasteResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.MemberService;
import freshtrash.freshtrashbackend.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static freshtrash.freshtrashbackend.config.constants.QueueType.*;
import static freshtrash.freshtrashbackend.dto.constants.AlarmMessage.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionApi {
    private final ChatRoomService chatRoomService;
    private final TransactionService transactionService;
    private final MemberService memberService;
    private final ApplicationEventPublisher eventPublisher;

    @GetMapping
    public ResponseEntity<Page<WasteResponse>> getTransactedWastes(
            @RequestParam TransactionMemberType memberType,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @PageableDefault(size = 6, sort = "createdAt", direction = DESC) Pageable pageable) {

        Page<WasteResponse> wastes = transactionService.getTransactedWastes(memberPrincipal.id(), memberType, pageable);
        return ResponseEntity.ok(wastes);
    }

    @PostMapping("/{wasteId}/chats/{chatRoomId}")
    public ResponseEntity<Void> completeTransaction(@PathVariable Long wasteId, @PathVariable Long chatRoomId) {
        log.debug("Start complete transaction...");
        ChatRoom closedChatRoom = chatRoomService.getChatRoom(chatRoomId);
        log.debug("Get closed chat room.");
        transactionService.completeTransaction(
                wasteId, chatRoomId, closedChatRoom.getSellerId(), closedChatRoom.getBuyerId(), SellStatus.CLOSE);
        log.debug("Executed complete transaction service");

        // 판매자, 구매자에게 알람 전송
        eventPublisher.publishEvent(AlarmEvent.of(
                WASTE_TRANSACTION_COMPLETE.getRoutingKey(),
                MessageRequest.builder()
                        .message(COMPLETED_SELL_MESSAGE.getMessage())
                        .wasteId(wasteId)
                        .memberId(closedChatRoom.getSellerId())
                        .fromMemberId(closedChatRoom.getBuyerId())
                        .alarmType(AlarmType.TRANSACTION)
                        .build()));
        log.debug("Send message to seller.");
        eventPublisher.publishEvent(AlarmEvent.of(
                WASTE_TRANSACTION_COMPLETE.getRoutingKey(),
                MessageRequest.builder()
                        .message(REQUEST_REVIEW_MESSAGE.getMessage())
                        .wasteId(wasteId)
                        .memberId(closedChatRoom.getBuyerId())
                        .fromMemberId(closedChatRoom.getSellerId())
                        .alarmType(AlarmType.TRANSACTION)
                        .build()));
        log.debug("Send message to buyer.");

        // 구매자가 아닌 사용자들에게 알람 전송
        chatRoomService.getChatRoomsByWasteId(wasteId, SellStatus.CLOSE).forEach(chatRoom -> {
            eventPublisher.publishEvent(AlarmEvent.of(
                    WASTE_TRANSACTION_COMPLETE.getRoutingKey(),
                    MessageRequest.builder()
                            .message(COMPLETED_SELL_MESSAGE.getMessage())
                            .wasteId(wasteId)
                            .memberId(chatRoom.getBuyerId())
                            .fromMemberId(chatRoom.getSellerId())
                            .alarmType(AlarmType.TRANSACTION)
                            .build()));
        });
        log.debug("Send message to not buyers.\nEnd complete transaction...");

        return ResponseEntity.ok(null);
    }

    /**
     * 판매상태 변경(예약중, 판매중) - 판매자가 변경
     */
    @PostMapping("/chats/{chatRoomId}/status")
    public ResponseEntity<Void> updateSellStatus(@PathVariable Long chatRoomId, @RequestParam SellStatus sellStatus) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId);
        transactionService.updateSellStatus(chatRoom.getWasteId(), chatRoomId, sellStatus);
        if (sellStatus == SellStatus.BOOKING) {
            // 구매자에게 예약중 알림 보내기
            eventPublisher.publishEvent(AlarmEvent.of(
                    WASTE_CHANGE_SELL_STATUS.getRoutingKey(),
                    MessageRequest.builder()
                            .message(String.format(
                                    BOOKING_MESSAGE.getMessage(),
                                    chatRoom.getSeller().getNickname()))
                            .wasteId(chatRoom.getWasteId())
                            .memberId(chatRoom.getBuyerId())
                            .fromMemberId(chatRoom.getSellerId())
                            .alarmType(AlarmType.TRANSACTION)
                            .build()));
        } else if (sellStatus == SellStatus.ONGOING) {
            // 해당 폐기물에 채팅요청했던 다른 구매자들에게 판매중 알림 보내기(현재 채팅방 구매자는 제외)
            chatRoomService
                    .getBuyerIdByWasteId(chatRoom.getWasteId(), chatRoom.getBuyerId())
                    .forEach(buyerIdSummary -> {
                        eventPublisher.publishEvent(AlarmEvent.of(
                                WASTE_CHANGE_SELL_STATUS.getRoutingKey(),
                                MessageRequest.builder()
                                        .message(String.format(
                                                ONGOING_MESSAGE.getMessage(),
                                                chatRoom.getSeller().getNickname()))
                                        .wasteId(chatRoom.getWasteId())
                                        .memberId(buyerIdSummary.buyerId())
                                        .fromMemberId(chatRoom.getSellerId())
                                        .alarmType(AlarmType.TRANSACTION)
                                        .build()));
                    });
        }

        return ResponseEntity.ok(null);
    }

    /**
     * 신고하기(채팅 상대방)
     */
    @PostMapping("/chats/{chatRoomId}/flag")
    public ResponseEntity<ApiResponse<Integer>> flagMember(
            @PathVariable Long chatRoomId, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId);

        // targetId: 채팅 상대방인 신고 받은 유저
        Long targetId = Objects.equals(memberPrincipal.id(), chatRoom.getSellerId())
                ? chatRoom.getBuyerId()
                : chatRoom.getSellerId();

        // flag_count + 1
        int flagCount = memberService.updateFlagCount(targetId).flagCount();
        String message = String.format(FLAG_MESSAGE.getMessage(), flagCount);

        if (flagCount >= 10) {
            message = EXCEED_FLAG_MESSAGE.getMessage();
        }
        // 신고받은 유저에게 알림 보내기
        eventPublisher.publishEvent(AlarmEvent.of(
                WASTE_TRANSACTION_FLAG.getRoutingKey(),
                MessageRequest.builder()
                        .message(message)
                        .wasteId(chatRoom.getWasteId())
                        .memberId(targetId)
                        .fromMemberId(memberPrincipal.id())
                        .alarmType(AlarmType.FLAG)
                        .build()));

        return ResponseEntity.ok(ApiResponse.of(flagCount));
    }
}
