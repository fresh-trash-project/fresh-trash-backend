package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.config.RabbitMQConfig;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.service.ChatService;
import freshtrash.freshtrashbackend.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionApi {
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange directExchange;
    private final ChatService chatService;
    private final TransactionService transactionService;

    private static final String COMPLETED_SELL_MESSAGE = "판매 완료되었습니다.";
    private static final String REQUEST_REVIEW_MESSAGE = "판매 완료되었습니다. 판매자에 대한 리뷰를 작성해주세요.";

    @PostMapping("/{wasteId}/chats/{chatRoomId}")
    public ResponseEntity<Void> completeTransaction(@PathVariable Long wasteId, @PathVariable Long chatRoomId) {
        ChatRoom closedChatRoom = chatService.getChatRoom(chatRoomId);
        transactionService.completeTransaction(
                wasteId, chatRoomId, closedChatRoom.getSellerId(), closedChatRoom.getBuyerId(), SellStatus.CLOSE);

        // 판매자, 구매자에게 알람 전송
        sendWasteTransactionMessage(COMPLETED_SELL_MESSAGE, wasteId, closedChatRoom.getSellerId());
        sendWasteTransactionMessage(REQUEST_REVIEW_MESSAGE, wasteId, closedChatRoom.getBuyerId());
        // 구매자가 아닌 사용자들에게 알람 전송
        chatService.getChatRoomsByWasteId(wasteId, SellStatus.CLOSE).forEach(chatRoom -> {
            sendWasteTransactionMessage(COMPLETED_SELL_MESSAGE, wasteId, chatRoom.getBuyerId());
        });

        return ResponseEntity.ok(null);
    }

    private Message buildMessage(String message, Long wasteId, Long memberId) {
        return MessageBuilder.withBody(message.getBytes())
                .setHeader(RabbitMQConfig.WASTE_ID_KEY, wasteId)
                .setHeader(RabbitMQConfig.MEMBER_ID_KEY, memberId)
                .build();
    }

    private void sendWasteTransactionMessage(String message, Long wasteId, Long memberId) {
        rabbitTemplate.convertAndSend(
                directExchange.getName(),
                RabbitMQConfig.WASTE_TRANSACTION_ROUTING_KEY,
                buildMessage(message, wasteId, memberId));
    }
}
