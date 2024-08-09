package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.domain.chatRoom.controller.constants.ProductEventType;
import freshtrash.freshtrashbackend.domain.chatRoom.controller.ChatRoomEventController;
import freshtrash.freshtrashbackend.domain.chatRoom.entity.ChatRoom;
import freshtrash.freshtrashbackend.domain.alarm.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.domain.chatRoom.entity.constants.ChatRoomSellStatus;
import freshtrash.freshtrashbackend.domain.product.entity.constants.ProductSellStatus;
import freshtrash.freshtrashbackend.domain.chatRoom.service.ChatRoomService;
import freshtrash.freshtrashbackend.domain.alarm.service.adapter.AlarmMappingHandlerAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.context.support.TestExecutionEvent.TEST_EXECUTION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(ChatRoomEventController.class)
@Import(TestSecurityConfig.class)
class ChatRoomEventControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ChatRoomService chatRoomService;

    @MockBean
    private AlarmMappingHandlerAdapter alarmMappingHandlerAdapter;

    @Test
    @DisplayName("신고하기")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void given_chatRoomIdAndMemberId_when_then_calledSendAlarmOfChatAlarm() throws Exception {
        // given
        Long currentMemberId = 123L, chatRoomId = 2L, productId = 1L, targetMemberId = 321L;
        ChatRoom chatRoom =
                Fixture.createChatRoom(productId, targetMemberId, currentMemberId, true, ChatRoomSellStatus.ONGOING);
        given(chatRoomService.getChatRoom(eq(chatRoomId), eq(currentMemberId))).willReturn(chatRoom);
        willDoNothing().given(alarmMappingHandlerAdapter).handle(eq(chatRoom), eq(currentMemberId), eq(AlarmType.FLAG));
        // when
        mvc.perform(post("/api/v1/chats/" + chatRoomId + "/flag")).andExpect(status().isOk());
        // then
    }

    @ParameterizedTest
    @DisplayName("중고 상품의 거래 상태 변경 처리")
    @CsvSource(value = {"CANCEL_BOOKING", "REQUEST_BOOKING", "COMPLETE_DEAL"})
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    void given_chatRoomIdAndEventType_when_then_calledSendAlarmOfProductAlarm(ProductEventType productEventType)
            throws Exception {
        // given
        Long chatRoomId = 2L, memberId = 123L;
        willDoNothing().given(alarmMappingHandlerAdapter).handle(chatRoomId, memberId, productEventType.getAlarmType());
        // when
        mvc.perform(post("/api/v1/chats/" + chatRoomId + "/productDeal")
                        .param("productEventType", productEventType.name()))
                .andExpect(status().isOk());
        // then
    }
}