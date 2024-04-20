package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.response.ChatRoomResponse;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.exception.ChatException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;

    public List<ChatRoom> getChatRoomsByWasteId(Long wasteId, SellStatus sellStatus) {
        return chatRoomRepository.findByWaste_IdAndSellStatusNot(wasteId, sellStatus);
    }

    public Page<ChatRoomResponse> getChatRoomsWithMemberId(Long memberId, Pageable pageable) {
        return chatRoomRepository
                .findAllBySeller_IdOrBuyer_Id(memberId, pageable)
                .map(ChatRoomResponse::fromEntity);
    }

    public ChatRoom getChatRoom(Long chatRoomId) {
        return chatRoomRepository
                .findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ErrorCode.NOT_FOUND_CHAT_ROOM));
    }

    public boolean isSellerOrBuyerOfChatRoom(Long chatRoomId, Long memberId) {
        return chatRoomRepository.existsByIdAndMemberId(chatRoomId, memberId);
    }
}
