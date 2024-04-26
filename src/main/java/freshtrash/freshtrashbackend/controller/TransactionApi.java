package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.config.RabbitMQConfig;
import freshtrash.freshtrashbackend.dto.constants.BookingStatus;
import freshtrash.freshtrashbackend.dto.constants.TransactionMemberType;
import freshtrash.freshtrashbackend.dto.request.MessageRequest;
import freshtrash.freshtrashbackend.dto.response.ApiResponse;
import freshtrash.freshtrashbackend.dto.response.WasteResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.exception.ChatRoomException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;

import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.MemberService;
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
    private final ChatRoomService chatRoomService;
    private final TransactionService transactionService;
    private final MemberService memberService;

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
        ChatRoom closedChatRoom = chatRoomService.getChatRoom(chatRoomId);
        transactionService.completeTransaction(
                wasteId, chatRoomId, closedChatRoom.getSellerId(), closedChatRoom.getBuyerId(), SellStatus.CLOSE);

        // 판매자, 구매자에게 알람 전송
        sendWasteTransactionMessage(MessageRequest.builder()
                .message(COMPLETED_SELL_MESSAGE.getMessage())
                .wasteId(wasteId)
                .memberId(closedChatRoom.getSellerId())
                .fromMemberId(closedChatRoom.getBuyerId())
                .alarmType(AlarmType.TRANSACTION)
                .build());
        sendWasteTransactionMessage(MessageRequest.builder()
                .message(REQUEST_REVIEW_MESSAGE.getMessage())
                .wasteId(wasteId)
                .memberId(closedChatRoom.getBuyerId())
                .fromMemberId(closedChatRoom.getSellerId())
                .alarmType(AlarmType.TRANSACTION)
                .build());

        // 구매자가 아닌 사용자들에게 알람 전송
        chatRoomService.getChatRoomsByWasteId(wasteId, SellStatus.CLOSE).forEach(chatRoom -> {
            sendWasteTransactionMessage(MessageRequest.builder()
                    .message(COMPLETED_SELL_MESSAGE.getMessage())
                    .wasteId(wasteId)
                    .memberId(chatRoom.getBuyerId())
                    .fromMemberId(chatRoom.getSellerId())
                    .alarmType(AlarmType.TRANSACTION)
                    .build());
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
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId);
        String message = chatRoom.getBuyer().getNickname() + REQUEST_BOOKING_MESSAGE.getMessage();
        // 예약신청은 구매자만 할 수 있다
        if (!Objects.equals(chatRoom.getBuyerId(), memberPrincipal.id())) {
            throw new ChatRoomException(ErrorCode.CANNOT_BOOKING_WITHOUT_BUYER);
        }
        // 판매자에게 알림 보내기
        sendWasteTransactionMessage(MessageRequest.builder()
                .message(message)
                .wasteId(wasteId)
                .memberId(chatRoom.getSellerId())
                .fromMemberId(chatRoom.getBuyerId())
                .alarmType(AlarmType.BOOKING_REQUEST)
                .build());

        return ResponseEntity.ok(null);
    }

    /**
     * 예약 요청 응답(판매자 -> 구매자)
     */
    @PostMapping("/{wasteId}/chats/{chatRoomId}/booking-reply")
    public ResponseEntity<Void> replyBooking(
            @PathVariable Long wasteId, @PathVariable Long chatRoomId, @RequestParam BookingStatus bookingStatus) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId);
        String message = chatRoom.getSeller().getNickname() + DECLINE_BOOKING_MESSAGE.getMessage();
        if (bookingStatus == BookingStatus.ACCEPT) {
            message = chatRoom.getSeller().getNickname() + ACCEPT_BOOKING_MESSAGE.getMessage();
            transactionService.updateSellStatus(wasteId, chatRoomId, SellStatus.BOOKING);
        }

        // 구매자에게 알림 전송
        sendWasteTransactionMessage(MessageRequest.builder()
                .message(message)
                .wasteId(wasteId)
                .memberId(chatRoom.getBuyerId())
                .fromMemberId(chatRoom.getSellerId())
                .alarmType(AlarmType.BOOKING_RESPONSE)
                .build());

        return ResponseEntity.ok(null);
    }

    /**
     * 판매 취소(구매자, 판매자 둘다 가능)
     */
    @PostMapping("/{wasteId}/chats/{chatRoomId}/cancel")
    public ResponseEntity<ApiResponse<Integer>> cancelTransaction(
            @PathVariable Long wasteId,
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId);
        String message = memberPrincipal.nickname() + CANCEL_TRANSACTION_MESSAGE.getMessage();
        // 판매중으로 변경
        transactionService.updateSellStatus(wasteId, chatRoomId, SellStatus.ONGOING);
        // cancel_count + 1
        int cancelCount = memberService.updateCancelCount(memberPrincipal).cancelCount();
        String alertMessage = cancelCount + CANCEL_ALERT_MESSAGE.getMessage();

        if (cancelCount >= 3) {
            alertMessage = BLACKLIST_MESSAGE.getMessage();
        }
        // 취소한 사람에게 알림 보내기
        sendWasteTransactionMessage(MessageRequest.builder()
                .message(alertMessage)
                .wasteId(wasteId)
                .memberId(memberPrincipal.id())
                .fromMemberId(0L)
                .alarmType(AlarmType.TRANSACTION)
                .build());

        // 구매자가 취소한 경우 -> 판매자에게 알림 / 판매자가 취소한 경우 -> 구매자에게 알림
        if (Objects.equals(chatRoom.getBuyerId(), memberPrincipal.id())) {
            sendWasteTransactionMessage(MessageRequest.builder()
                    .message(message)
                    .wasteId(wasteId)
                    .memberId(chatRoom.getSellerId())
                    .fromMemberId(chatRoom.getBuyerId())
                    .alarmType(AlarmType.TRANSACTION)
                    .build());
        } else {
            sendWasteTransactionMessage(MessageRequest.builder()
                    .message(message)
                    .wasteId(wasteId)
                    .memberId(chatRoom.getBuyerId())
                    .fromMemberId(chatRoom.getSellerId())
                    .alarmType(AlarmType.TRANSACTION)
                    .build());
        }

        return ResponseEntity.ok(ApiResponse.of(cancelCount));
    }

    private Message buildMessage(MessageRequest messageRequest) {
        return MessageBuilder.withBody(messageRequest.message().getBytes())
                .setHeader(RabbitMQConfig.WASTE_ID_KEY, messageRequest.wasteId())
                .setHeader(RabbitMQConfig.MEMBER_ID_KEY, messageRequest.memberId())
                .setHeader(RabbitMQConfig.FROM_MEMBER_ID_KEY, messageRequest.fromMemberId())
                .setHeader(RabbitMQConfig.ALARM_TYPE, messageRequest.alarmType())
                .build();
    }

    private void sendWasteTransactionMessage(MessageRequest messageRequest) {
        rabbitTemplate.convertAndSend(
                directExchange.getName(), RabbitMQConfig.WASTE_TRANSACTION_ROUTING_KEY, buildMessage(messageRequest));
    }
}
