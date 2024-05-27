package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.controller.constants.ProductEventType;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.alarm.CancelBookingProductAlarm;
import freshtrash.freshtrashbackend.service.alarm.CompleteDealProductAlarm;
import freshtrash.freshtrashbackend.service.alarm.RequestBookingProductAlarm;
import freshtrash.freshtrashbackend.service.alarm.UserFlagChatAlarm;
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
@WebMvcTest(ChatRoomEventApi.class)
@Import(TestSecurityConfig.class)
class ChatRoomEventApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ChatRoomService chatRoomService;

    @MockBean
    private UserFlagChatAlarm userFlagChatAlarm;

    @MockBean
    private CancelBookingProductAlarm cancelBookingProductAlarm;

    @MockBean
    private CompleteDealProductAlarm completeDealProductAlarm;

    @MockBean
    private RequestBookingProductAlarm requestBookingProductAlarm;

    @Test
    @DisplayName("신고하기")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void given_chatRoomIdAndMemberId_when_then_calledSendAlarmOfChatAlarm() throws Exception {
        // given
        Long currentMemberId = 123L, chatRoomId = 2L, wasteId = 1L, targetMemberId = 321L;
        ChatRoom chatRoom = Fixture.createChatRoom(wasteId, targetMemberId, currentMemberId, true, SellStatus.ONGOING);
        given(chatRoomService.getChatRoom(eq(chatRoomId))).willReturn(chatRoom);
        willDoNothing().given(userFlagChatAlarm).sendAlarm(eq(chatRoom), eq(currentMemberId));
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
        Long chatRoomId = 2L;
        switch (productEventType) {
            case CANCEL_BOOKING -> willDoNothing()
                    .given(cancelBookingProductAlarm)
                    .sendAlarm(eq(chatRoomId));
            case REQUEST_BOOKING -> willDoNothing()
                    .given(requestBookingProductAlarm)
                    .sendAlarm(eq(chatRoomId));
            default -> willDoNothing().given(completeDealProductAlarm).sendAlarm(chatRoomId);
        }
        // when
        mvc.perform(post("/api/v1/chats/" + chatRoomId + "/transaction")
                        .param("productEventType", productEventType.name()))
                .andExpect(status().isOk());
        // then
    }
}