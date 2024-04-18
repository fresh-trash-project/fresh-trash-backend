package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.exception.ChatRoomException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.ChatRoomRepository;
import freshtrash.freshtrashbackend.repository.WasteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final WasteRepository wasteRepository;

    public ChatRoom getOrCreateChatRoom(Long wasteId, Long buyerId) {
        Waste waste =
                wasteRepository.findById(wasteId).orElseThrow(() -> new ChatRoomException(ErrorCode.NOT_FOUND_WASTE));
        Long sellerId = waste.getMember().getId();

        // 판매자가 자신이 등록한 폐기물에 대해 채팅을 시도하는 경우 예외 처리
        if (sellerId.equals(buyerId)) {
            throw new ChatRoomException(ErrorCode.CANNOT_CHAT_WITH_SELF);
        }

        // 기존 채팅방 검색
        return chatRoomRepository
                .findBySellerIdAndBuyerIdAndWasteId(sellerId, buyerId, wasteId)
                .orElseGet(() -> createChatRoom(waste, buyerId));
    }

    private ChatRoom createChatRoom(Waste waste, Long buyerId) {
        return chatRoomRepository.save(ChatRoom.builder()
                .waste(waste)
                .sellerId(waste.getMember().getId())
                .buyerId(buyerId)
                .sellStatus(waste.getSellStatus())
                .openOrClose(true)
                .build());
    }
}