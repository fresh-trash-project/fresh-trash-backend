package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.response.ChatRoomResponse;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.exception.ChatException;
import freshtrash.freshtrashbackend.exception.ChatRoomException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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

    public ChatRoom getChatRoom(Long chatRoomId, Long memberId) {
        checkIfSellerOrBuyerOfChatRoom(chatRoomId, memberId);
        return chatRoomRepository
                .findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ErrorCode.NOT_FOUND_CHAT_ROOM));
    }

    public ChatRoom getOrCreateChatRoom(Long sellerId, Long buyerId, Long productId) {
        checkIfSellerOfProduct(sellerId, buyerId);
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

    public void closeChatRoom(Long chatRoomId) {
        chatRoomRepository.updateOpenOrClose(chatRoomId);
    }

    /**
     * productId에 해당하며 전달받은 SellStatus가 아닌 채팅방들을 조회
     */
    private List<ChatRoom> getChatRoomsByProductIdAndNotSellStatus(Long productId, SellStatus sellStatus) {
        return chatRoomRepository.findByProduct_IdAndSellStatusNot(productId, sellStatus);
    }

    /**
     * 판매자 또는 구매자만이 대상 채팅방을 조회할 수 있습니다
     */
    private void checkIfSellerOrBuyerOfChatRoom(Long chatRoomId, Long memberId) {
        log.debug("채팅방을 조회할 수 있는 판매자 또는 구매자인지 확인... chatRoomId: {}, memberId: {}", chatRoomId, memberId);
        if (!chatRoomRepository.existsByIdAndMemberId(chatRoomId, memberId))
            throw new ChatException(ErrorCode.FORBIDDEN_CHAT_ROOM);
    }

    /**
     * 판매자가 자신이 등록한 폐기물에 대해 채팅을 시도하는 경우 예외 처리
     */
    private void checkIfSellerOfProduct(Long sellerId, Long buyerId) {
        if (sellerId.equals(buyerId)) throw new ChatRoomException(ErrorCode.CANNOT_CHAT_WITH_SELF);
    }
}
