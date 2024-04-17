package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.exception.ChatRoomException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.ChatRoomRepository;
import freshtrash.freshtrashbackend.repository.MemberRepository;
import freshtrash.freshtrashbackend.repository.WasteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final WasteRepository wasteRepository;

    public ChatRoom createChatRoom(Long wasteId, Long buyerId) {
        Waste waste =
                wasteRepository.findById(wasteId).orElseThrow(() -> new ChatRoomException(ErrorCode.WASTE_NOT_FOUND));
        Member seller = waste.getMember();

        ChatRoom chatRoom = ChatRoom.builder()
                .waste(waste)
                .sellerId(seller.getId())
                .buyerId(buyerId)
                .sellStatus(waste.getSellStatus())
                .openOrClose(true)
                .build();

        return chatRoomRepository.save(chatRoom);
    }
}