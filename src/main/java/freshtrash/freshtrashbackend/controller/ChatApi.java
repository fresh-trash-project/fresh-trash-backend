package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.response.ChatRoomResponse;
import freshtrash.freshtrashbackend.dto.response.ChatRoomWithMessagesResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.exception.ChatException;
import freshtrash.freshtrashbackend.exception.ChatRoomException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.ChatService;
import freshtrash.freshtrashbackend.service.WasteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wastes/{wasteId}/chats")
@RequiredArgsConstructor
public class ChatApi {
    private final ChatService chatService;
    private final ChatRoomService chatRoomService;
    private final WasteService wasteService;

    @GetMapping
    public ResponseEntity<Page<ChatRoomResponse>> getChatRooms(
            @PageableDefault Pageable pageable, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        return ResponseEntity.ok(chatService.getChatRoomsWithMemberId(memberPrincipal.id(), pageable));
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatRoomWithMessagesResponse> getChatRoomWithMessages(
            @PathVariable Long chatRoomId, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        checkIfSellerOrBuyerOfChatRoom(chatRoomId, memberPrincipal.id());
        return ResponseEntity.ok(ChatRoomWithMessagesResponse.fromEntity(chatService.getChatRoom(chatRoomId)));
    }

    /**
     * 채팅 요청
     */
    @PostMapping
    public ResponseEntity<ChatRoomResponse> handleChatRoomRequest(
            @PathVariable Long wasteId, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        Waste waste = wasteService.getWaste(wasteId);
        Member seller = waste.getMember();
        checkIfSellerOfWaste(memberPrincipal.id(), seller.getId());

        ChatRoom chatRoom = chatRoomService.getOrCreateChatRoom(seller.getId(), memberPrincipal.id(), wasteId);

        ChatRoomResponse response =
                ChatRoomResponse.fromEntity(chatRoom, waste.getTitle(), seller.getNickname(), memberPrincipal.nickname());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 판매자 또는 구매자만이 대상 채팅방을 조회할 수 있습니다
     */
    private void checkIfSellerOrBuyerOfChatRoom(Long chatRoomId, Long memberId) {
        if (!chatService.isSellerOrBuyerOfChatRoom(chatRoomId, memberId))
            throw new ChatException(ErrorCode.FORBIDDEN_CHAT_ROOM);
    }

    /**
     * 판매자가 자신이 등록한 폐기물에 대해 채팅을 시도하는 경우 예외 처리
     */
    private void checkIfSellerOfWaste(Long buyerId, Long sellerId) {
        if (sellerId.equals(buyerId)) throw new ChatRoomException(ErrorCode.CANNOT_CHAT_WITH_SELF);
    }
}