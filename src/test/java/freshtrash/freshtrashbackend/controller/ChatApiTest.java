package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.dto.response.ChatRoomResponse;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.service.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfig.class)
@WebMvcTest(ChatApi.class)
class ChatApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ChatService chatService;

    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("채팅방 목록 조회")
    @Test
    void given_memberIdAndPageable_when_getChatRooms_then_returnPagingChatRoomsResponse() throws Exception {
        // given
        Long wasteId = 1L;
        int expectedSize = 1;
        given(chatService.getChatRooms(anyLong(), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(ChatRoomResponse.fromEntity(Fixture.createChatRoom()))));
        // when
        mvc.perform(get("/api/v1/wastes/" + wasteId + "/chats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(expectedSize));
        // then
    }

    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("채팅방 + 채팅 메시지 조회")
    @Test
    void given_chatRoomIdAndMemberId_when_ifSellerOrBuyerOfChatRoom_then_getChatRoomWithMessages() throws Exception {
        // given
        Long wasteId = 1L;
        Long chatRoomId = 2L;
        ChatRoom chatRoom = Fixture.createChatRoom();
        given(chatService.isSellerOrBuyerOfChatRoom(anyLong(), anyLong())).willReturn(true);
        given(chatService.getChatRoom(anyLong())).willReturn(chatRoom);
        // when
        mvc.perform(get("/api/v1/wastes/" + wasteId + "/chats/" + chatRoomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chatRoom.sellerNickname").value(chatRoom.getSeller().getNickname()))
                .andExpect(jsonPath("$.chatRoom.buyerNickname").value(chatRoom.getBuyer().getNickname()))
                .andExpect(jsonPath("$.messages.size()").value(chatRoom.getChatMessages().size()));
        // then
    }
}