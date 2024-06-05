package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.response.ChatRoomResponse;
import freshtrash.freshtrashbackend.dto.response.ChatRoomWithMessagesResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
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
public class ChatRoomApi {
    private final ChatRoomService chatRoomService;

    @GetMapping
    public ResponseEntity<Page<ChatRoomResponse>> getChatRooms(
            @PageableDefault Pageable pageable, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        return ResponseEntity.ok(chatRoomService.getChatRoomsWithMemberId(memberPrincipal.id(), pageable));
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatRoomWithMessagesResponse> getChatRoomWithMessages(
            @PathVariable Long chatRoomId, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        return ResponseEntity.ok(
                ChatRoomWithMessagesResponse.fromEntity(chatRoomService.getChatRoom(chatRoomId, memberPrincipal.id())));
    }

    /**
     * 채팅 나가기
     */
    @PutMapping("/{chatRoomId}")
    public ResponseEntity<Void> closeChatRoom(@PathVariable Long chatRoomId) {
        chatRoomService.closeChatRoom(chatRoomId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}