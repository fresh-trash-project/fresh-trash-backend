package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.config.RabbitMQConfig;
import freshtrash.freshtrashbackend.dto.constants.BookingStatus;
import freshtrash.freshtrashbackend.dto.constants.TransactionMemberType;
import freshtrash.freshtrashbackend.dto.response.WasteResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.exception.ChatRoomException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.service.ChatService;
import freshtrash.freshtrashbackend.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static freshtrash.freshtrashbackend.dto.constants.AlarmMessage.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionApi {
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange directExchange;
    private final ChatService chatService;
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<WasteResponse>> getTransactedWastes(
            @RequestParam TransactionMemberType memberType,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @PageableDefault(sort = "createdAt", direction = DESC) Pageable pageable) {

        Page<WasteResponse> wastes = transactionService.getTransactedWastes(memberPrincipal.id(), memberType, pageable);
        return ResponseEntity.ok(wastes);
    }

    @PostMapping("/{wasteId}/chats/{chatRoomId}")
    public ResponseEntity<Void> completeTransaction(@PathVariable Long wasteId, @PathVariable Long chatRoomId) {
        ChatRoom closedChatRoom = chatService.getChatRoom(chatRoomId);
        transactionService.completeTransaction(
                wasteId, chatRoomId, closedChatRoom.getSellerId(), closedChatRoom.getBuyerId(), SellStatus.CLOSE);

        // 판매자, 구매자에게 알람 전송
        sendWasteTransactionMessage(
                COMPLETED_SELL_MESSAGE.getMessage(),
                wasteId,
                closedChatRoom.getSellerId(),
                closedChatRoom.getBuyerId());
        sendWasteTransactionMessage(
                REQUEST_REVIEW_MESSAGE.getMessage(),
                wasteId,
                closedChatRoom.getBuyerId(),
                closedChatRoom.getSellerId());
        // 구매자가 아닌 사용자들에게 알람 전송
        chatService.getChatRoomsByWasteId(wasteId, SellStatus.CLOSE).forEach(chatRoom -> {
            sendWasteTransactionMessage(
                    COMPLETED_SELL_MESSAGE.getMessage(), wasteId, chatRoom.getBuyerId(), closedChatRoom.getSellerId());
        });

        return ResponseEntity.ok(null);
    }

    /**
     * 예약 신청(구매자 -> 판매자)
     */
    @PostMapping("/{wasteId}/chats/{chatRoomId}/booking")
    public ResponseEntity<Void> sendBooking(
            @PathVariable Long wasteId,
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        ChatRoom chatRoom = chatService.getChatRoom(chatRoomId);
        String message = chatRoom.getBuyer().getNickname() + REQUEST_BOOKING_MESSAGE.getMessage();
        // 예약신청은 구매자만 할 수 있다
        if (!Objects.equals(chatRoom.getBuyerId(), memberPrincipal.id())) {
            throw new ChatRoomException(ErrorCode.CANNOT_BOOKING_WITHOUT_BUYER);
        }
        // 판매자에게 알림 보내기
        sendWasteTransactionMessage(message, wasteId, chatRoom.getSellerId(), chatRoom.getBuyerId());

        return ResponseEntity.ok(null);
    }

    /**
     * 예약 요청 응답(판매자 -> 구매자)
     */
    @PostMapping("/{wasteId}/chats/{chatRoomId}/booking-reply")
    public ResponseEntity<Void> replyBooking(
            @PathVariable Long wasteId, @PathVariable Long chatRoomId, @RequestParam BookingStatus bookingStatus) {
        ChatRoom chatRoom = chatService.getChatRoom(chatRoomId);
        String message = chatRoom.getSeller().getNickname() + DECLINE_BOOKING_MESSAGE.getMessage();
        if (bookingStatus == BookingStatus.ACCEPT) {
            message = chatRoom.getSeller().getNickname() + ACCEPT_BOOKING_MESSAGE.getMessage();
            transactionService.updateSellStatus(wasteId, chatRoomId, SellStatus.BOOKING);
        }

        // 구매자에게 알림 전송
        sendWasteTransactionMessage(message, wasteId, chatRoom.getBuyerId(), chatRoom.getSellerId());

        return ResponseEntity.ok(null);
    }

    private Message buildMessage(String message, Long wasteId, Long memberId, Long fromMemberId) {
        return MessageBuilder.withBody(message.getBytes())
                .setHeader(RabbitMQConfig.WASTE_ID_KEY, wasteId)
                .setHeader(RabbitMQConfig.MEMBER_ID_KEY, memberId)
                .setHeader(RabbitMQConfig.FROM_MEMBER_ID_KEY, fromMemberId)
                .build();
    }

    private void sendWasteTransactionMessage(String message, Long wasteId, Long memberId, Long fromMemberId) {
        rabbitTemplate.convertAndSend(
                directExchange.getName(),
                RabbitMQConfig.WASTE_TRANSACTION_ROUTING_KEY,
                buildMessage(message, wasteId, memberId, fromMemberId));
    }
}
