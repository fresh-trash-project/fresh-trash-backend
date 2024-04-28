package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.dto.response.ChatRoomResponse;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.WasteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfig.class)
@WebMvcTest(ChatApi.class)
class ChatApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ChatRoomService chatRoomService;

    @MockBean
    private WasteService wasteService;

    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("채팅방 목록 조회")
    @Test
    void given_memberIdAndPageable_when_getChatRooms_then_returnPagingChatRoomsResponse() throws Exception {
        // given
        Long wasteId = 1L;
        Long memberId = 123L;
        int expectedSize = 1;
        Pageable pageable = PageRequest.of(0, 10);
        given(chatRoomService.getChatRoomsWithMemberId(eq(memberId), eq(pageable)))
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
        Long memberId = 123L;
        Long chatRoomId = 2L;
        ChatRoom chatRoom = Fixture.createChatRoom();
        given(chatRoomService.isSellerOrBuyerOfChatRoom(eq(chatRoomId), eq(memberId)))
                .willReturn(true);
        given(chatRoomService.getChatRoom(eq(chatRoomId))).willReturn(chatRoom);
        // when
        mvc.perform(get("/api/v1/wastes/" + wasteId + "/chats/" + chatRoomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chatRoom.wasteTitle")
                        .value(chatRoom.getWaste().getTitle()))
                .andExpect(jsonPath("$.chatRoom.sellerNickname")
                        .value(chatRoom.getSeller().getNickname()))
                .andExpect(jsonPath("$.chatRoom.buyerNickname")
                        .value(chatRoom.getBuyer().getNickname()))
                .andExpect(jsonPath("$.messages.size()")
                        .value(chatRoom.getChatMessages().size()));
        // then
    }

    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("채팅 요청")
    @Test
    void given_wasteIdAndLoginUser_when_notSeller_then_returnChatRoomAfterGetOrCreateChatRoom() throws Exception {
        // given
        Long wasteId = 1L;
        Long buyerId = 123L;
        Long sellerId = 1L;
        String buyerNickname = "testUser";
        Waste waste = Fixture.createWaste();
        ChatRoom chatRoom = Fixture.createChatRoom();
        given(wasteService.getWaste(eq(wasteId))).willReturn(waste);
        given(chatRoomService.getOrCreateChatRoom(eq(sellerId), eq(buyerId), eq(wasteId)))
                .willReturn(chatRoom);
        // when
        mvc.perform(post("/api/v1/wastes/" + wasteId + "/chats"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.wasteTitle").value(waste.getTitle()))
                .andExpect(jsonPath("$.sellStatus").value("ONGOING"))
                .andExpect(jsonPath("$.sellerNickname").value(waste.getMember().getNickname()))
                .andExpect(jsonPath("$.buyerNickname").value(buyerNickname));
        // then
    }
}