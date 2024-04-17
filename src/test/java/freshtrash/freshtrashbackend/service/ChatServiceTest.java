package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.repository.ChatRoomRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    @InjectMocks
    private ChatService chatService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @DisplayName("Waste id로 채팅방 목록 조회")
    @Test
    void given_wasteId_when_then_getChatRoomList() {
        // given
        Long wasteId = 1L;
        int expectedSize = 1;
        given(chatRoomRepository.findByWaste_Id(anyLong())).willReturn(List.of(Fixture.createChatRoom()));
        // when
        List<ChatRoom> chatRooms = chatService.getChatRoomsByWasteId(wasteId);
        // then
        Assertions.assertThat(chatRooms.size()).isEqualTo(expectedSize);
    }
}
