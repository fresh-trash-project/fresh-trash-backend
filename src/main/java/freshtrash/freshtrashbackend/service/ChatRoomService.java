package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
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

    public ChatRoom getOrCreateChatRoom(Long sellerId, Long buyerId, Long wasteId) {

        // 기존 채팅방 검색
        return chatRoomRepository
                .findBySellerIdAndBuyerIdAndWasteId(sellerId, buyerId, wasteId)
                .orElseGet(() -> chatRoomRepository.save(ChatRoom.builder()
                                .wasteId(wasteId)
                                .sellerId(sellerId)
                                .buyerId(buyerId)
                                .sellStatus(SellStatus.ONGOING)
                                .openOrClose(true)
                        .build()));
    }
}