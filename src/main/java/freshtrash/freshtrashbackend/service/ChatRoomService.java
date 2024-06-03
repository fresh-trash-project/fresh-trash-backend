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
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    /**
     * ProductId에 속한 Close 되지 않은 채팅방 모두 조회
     */
    public List<ChatRoom> getNotClosedChatRoomsByProductId(Long productId) {
        return getChatRoomsByProductIdAndNotSellStatus(productId, SellStatus.CLOSE);
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

    public ChatRoom getOrCreateChatRoom(Long sellerId, Long buyerId, Long productId) {

        // 기존 채팅방 검색
        return chatRoomRepository
                .findBySellerIdAndBuyerIdAndProductId(sellerId, buyerId, productId)
                .orElseGet(() -> chatRoomRepository.save(ChatRoom.builder()
                        .productId(productId)
                        .sellerId(sellerId)
                        .buyerId(buyerId)
                        .sellStatus(SellStatus.ONGOING)
                        .openOrClose(true)
                        .build()));
    }

    public boolean isSellerOrBuyerOfChatRoom(Long chatRoomId, Long memberId) {
        return chatRoomRepository.existsByIdAndMemberId(chatRoomId, memberId);
    }

    public void closeChatRoom(Long chatRoomId) {
        chatRoomRepository.updateOpenOrClose(chatRoomId);
    }

    /**
     * productId에 해당하며 전달받은 SellStatus가 아닌 채팅방들을 조회
     */
    private List<ChatRoom> getChatRoomsByProductIdAndNotSellStatus(Long productId, SellStatus sellStatus) {
        return chatRoomRepository.findByProduct_IdAndSellStatusNot(productId, sellStatus);
    }
}
