package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.response.ChatRoomResponse;
import freshtrash.freshtrashbackend.dto.response.ChatRoomWithMessagesResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.exception.ChatException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatApi {
    private final ChatRoomService chatRoomService;

    @GetMapping
    public ResponseEntity<Page<ChatRoomResponse>> getChatRooms(
            @PageableDefault Pageable pageable, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        return ResponseEntity.ok(chatRoomService.getChatRoomsWithMemberId(memberPrincipal.id(), pageable));
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatRoomWithMessagesResponse> getChatRoomWithMessages(
            @PathVariable Long chatRoomId, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        checkIfSellerOrBuyerOfChatRoom(chatRoomId, memberPrincipal.id());
        return ResponseEntity.ok(ChatRoomWithMessagesResponse.fromEntity(chatRoomService.getChatRoom(chatRoomId)));
    }

    /**
     * 채팅 나가기
     */
    @PutMapping("/{chatRoomId}")
    public ResponseEntity<Void> closeChatRoom(@PathVariable Long chatRoomId) {
        chatRoomService.closeChatRoom(chatRoomId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    /**
     * 판매자 또는 구매자만이 대상 채팅방을 조회할 수 있습니다
     */
    private void checkIfSellerOrBuyerOfChatRoom(Long chatRoomId, Long memberId) {
        if (!chatRoomService.isSellerOrBuyerOfChatRoom(chatRoomId, memberId))
            throw new ChatException(ErrorCode.FORBIDDEN_CHAT_ROOM);
    }
}