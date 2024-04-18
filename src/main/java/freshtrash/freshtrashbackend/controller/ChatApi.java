package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.response.ChatRoomResponse;
import freshtrash.freshtrashbackend.dto.response.ChatRoomWithMessagesResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.exception.ChatException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wastes/{wasteId}/chats")
@RequiredArgsConstructor
public class ChatApi {
    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<Page<ChatRoomResponse>> getChatRooms(
            @PageableDefault Pageable pageable, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        return ResponseEntity.ok(chatService.getChatRooms(memberPrincipal.id(), pageable));
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatRoomWithMessagesResponse> getChatRoomWithMessages(
            @PathVariable Long chatRoomId, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        checkIfSellerOrBuyerOfChatRoom(chatRoomId, memberPrincipal.id());
        return ResponseEntity.ok(ChatRoomWithMessagesResponse.fromEntity(chatService.getChatRoom(chatRoomId)));
    }

    /**
     * 판매자 또는 구매자만이 대상 채팅방을 조회할 수 있습니다
     */
    private void checkIfSellerOrBuyerOfChatRoom(Long chatRoomId, Long memberId) {
        if (!chatService.isSellerOrBuyerOfChatRoom(chatRoomId, memberId))
            throw new ChatException(ErrorCode.FORBIDDEN_CHAT_ROOM);
    }
}
