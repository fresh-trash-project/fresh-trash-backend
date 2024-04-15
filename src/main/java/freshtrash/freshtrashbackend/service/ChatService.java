package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;

    public List<ChatRoom> getChatRoomsByWasteId(Long wasteId) {
        return chatRoomRepository.findByWaste_Id(wasteId);
    }
}
