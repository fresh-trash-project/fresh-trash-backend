package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.dto.response.ChatRoomResponse;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.repository.ChatRoomRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

    @DisplayName("채팅방 목록 조회")
    @Test
    void given_memberIdAndPageable_when_getChatRooms_then_returnPagingChatRoomsResponse() {
        // given
        Long memberId = 1L;
        int expectedSize = 1;
        Pageable pageable = PageRequest.of(0, 10);
        given(chatRoomRepository.findAllBySeller_IdOrBuyer_Id(anyLong(), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(Fixture.createChatRoom())));
        // when
        Page<ChatRoomResponse> chatRooms = chatService.getChatRooms(memberId, pageable);
        // then
        assertThat(chatRooms.getSize()).isEqualTo(expectedSize);
    }

    @DisplayName("채팅방 단일 조회")
    @Test
    void given_chatRoomId_when_getChatRoom_then_returnChatRoom() {
        // given
        Long chatRoomId = 1L;
        given(chatRoomRepository.findById(anyLong())).willReturn(Optional.of(Fixture.createChatRoom()));
        // when
        ChatRoom chatRoom = chatService.getChatRoom(chatRoomId);
        // then
        assertThat(chatRoom).isNotNull();
    }
}
