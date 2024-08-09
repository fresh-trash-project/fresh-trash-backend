package freshtrash.freshtrashbackend.domain.chatMessage.controller;

import freshtrash.freshtrashbackend.domain.chatMessage.dto.request.ChatMessageRequest;
import freshtrash.freshtrashbackend.domain.chatMessage.dto.response.ChatMessageResponse;
import freshtrash.freshtrashbackend.domain.chatMessage.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    /**
     * 채팅 pub/sub
     * @param chatRoomId 채팅 중인 채팅방 id
     * @param memberId 메시지를 전송한 유저 id
     */
    @MessageMapping("/{chatRoomId}/message/{memberId}")
    @SendTo("/topic/chats.{chatRoomId}")
    public ChatMessageResponse sendChatMessage(
            @DestinationVariable Long chatRoomId,
            @DestinationVariable Long memberId,
            ChatMessageRequest chatMessageRequest) {

        return ChatMessageResponse.fromEntity(
                chatMessageService.saveChatMessage(chatRoomId, memberId, chatMessageRequest.message()));
    }
}
