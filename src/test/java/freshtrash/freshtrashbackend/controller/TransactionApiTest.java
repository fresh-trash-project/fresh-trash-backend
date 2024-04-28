package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.config.RabbitMQConfig;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.dto.constants.BookingStatus;
import freshtrash.freshtrashbackend.dto.constants.TransactionMemberType;
import freshtrash.freshtrashbackend.dto.response.WasteResponse;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.context.support.TestExecutionEvent.TEST_EXECUTION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfig.class)
@WebMvcTest(TransactionApi.class)
class TransactionApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private DirectExchange directExchange;

    @MockBean
    private ChatRoomService chatRoomService;

    @MockBean
    private TransactionService transactionService;

    @DisplayName("거래 완료 후 알람 전송 + 거래 내역 저장")
    @Test
    void given_wasteId_when_getChatRooms_then_sendAlarmsToSellerAndBuyerAndNotBuyers() throws Exception {
        // given
        Long wasteId = 1L;
        Long chatRoomId = 5L;
        Long sellerId = 1L;
        Long buyerId = 2L;
        Long notBuyerId = 3L;
        given(chatRoomService.getChatRoom(eq(chatRoomId)))
                .willReturn(Fixture.createChatRoom(wasteId, sellerId, buyerId, true, SellStatus.CLOSE));
        willDoNothing()
                .given(transactionService)
                .completeTransaction(eq(wasteId), eq(chatRoomId), eq(sellerId), eq(buyerId), eq(SellStatus.CLOSE));
        given(chatRoomService.getChatRoomsByWasteId(eq(wasteId), eq(SellStatus.CLOSE)))
                .willReturn(List.of(Fixture.createChatRoom(wasteId, sellerId, notBuyerId, true, SellStatus.ONGOING)));
        willDoNothing()
                .given(rabbitTemplate)
                .convertAndSend(
                        anyString(),
                        anyString(),
                        any(Message.class));
        // when
        mvc.perform(post("/api/v1/transactions/" + wasteId + "/chats/" + chatRoomId))
                .andExpect(status().isOk());
        // then
    }

    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    @DisplayName("판매/구매 폐기물 목록 조회")
    @ParameterizedTest
    @CsvSource(value = {"SELLER_ONGOING", "SELLER_CLOSE", "BUYER"})
    void given_memberTypeAndLoginUserAndPageable_when_then_getPagingWasteData(TransactionMemberType memberType)
            throws Exception {
        // given
        Long memberId = 123L;
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "createdAt"));
        given(transactionService.getTransactedWastes(eq(memberId), eq(memberType), eq(pageable)))
                .willReturn(new PageImpl<>(List.of(WasteResponse.fromEntity(Fixture.createWaste()))));
        // when
        mvc.perform(get("/api/v1/transactions").queryParam("memberType", memberType.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(1));

        // then
    }

    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    @DisplayName("예약 신청")
    @Test
    void given_wasteIdAndChatRoomIdAndLoginUser_when_userIsBuyer_then_sendAlarmsToSeller() throws Exception {
        // given
        Long wasteId = 1L;
        Long chatRoomId = 5L;
        Long sellerId = 3L;
        Long buyerId = 123L;
        given(chatRoomService.getChatRoom(eq(chatRoomId)))
                .willReturn(Fixture.createChatRoom(wasteId, sellerId, buyerId, true, SellStatus.ONGOING));
        willDoNothing().given(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Message.class));

        // when
        mvc.perform(post("/api/v1/transactions/" + wasteId + "/chats/" + chatRoomId + "/booking"))
                .andExpect(status().isOk());
        // then
    }

    @DisplayName("예약 신청 응답 - 승낙")
    @Test
    void given_wasteIdAndChatRoomIdAndBookingStatus_when_bookingAccept_then_updateSellStatusAndsendAlarmToBuyer()
            throws Exception {
        // given
        Long wasteId = 2L;
        Long chatRoomId = 4L;
        given(chatRoomService.getChatRoom(anyLong()))
                .willReturn(Fixture.createChatRoom(wasteId, 2L, 3L, true, SellStatus.ONGOING));
        willDoNothing().given(transactionService).updateSellStatus(anyLong(), anyLong(), eq(SellStatus.BOOKING));
        willDoNothing().given(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Message.class));
        // when
        mvc.perform(post("/api/v1/transactions/" + wasteId + "/chats/" + chatRoomId)
                        .queryParam("bookingStatus", BookingStatus.ACCEPT.name()))
                .andExpect(status().isOk());
        // then
    }

    @DisplayName("예약 신청 응답 - 거절")
    @Test
    void given_wasteIdAndChatRoomIdAndBookingStatus_when_bookingDecline_then_sendAlarmToBuyer() throws Exception {
        // given
        Long wasteId = 1L;
        Long chatRoomId = 5L;
        given(chatRoomService.getChatRoom(anyLong()))
                .willReturn(Fixture.createChatRoom(wasteId, 1L, 2L, true, SellStatus.ONGOING));
        willDoNothing().given(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Message.class));
        // when
        mvc.perform(post("/api/v1/transactions/" + wasteId + "/chats/" + chatRoomId)
                        .queryParam("bookingStatus", BookingStatus.DECLINE.name()))
                .andExpect(status().isOk());
        // then
    }
}