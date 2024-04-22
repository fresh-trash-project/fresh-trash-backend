package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.dto.constants.TransactionMemberType;
import freshtrash.freshtrashbackend.dto.response.WasteResponse;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.service.ChatService;
import freshtrash.freshtrashbackend.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
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
    private ChatService chatService;

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
        given(chatService.getChatRoom(anyLong()))
                .willReturn(Fixture.createChatRoom(wasteId, sellerId, buyerId, true, SellStatus.CLOSE));
        willDoNothing()
                .given(transactionService)
                .completeTransaction(anyLong(), anyLong(), anyLong(), anyLong(), eq(SellStatus.CLOSE));
        given(chatService.getChatRoomsByWasteId(anyLong(), eq(SellStatus.CLOSE)))
                .willReturn(List.of(Fixture.createChatRoom(wasteId, sellerId, notBuyerId, true, SellStatus.ONGOING)));
        willDoNothing().given(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Message.class));
        // when
        mvc.perform(post("/api/v1/transactions/" + wasteId + "/chats/" + chatRoomId))
                .andExpect(status().isOk());
        // then
    }

    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    @DisplayName("판매/구매 폐기물 목록 조회")
    @ParameterizedTest
    @CsvSource(value = {"SELLER", "BUYER"})
    void given_memberTypeAndLoginUserAndPageable_when_then_getPagingWasteData(TransactionMemberType memberType) throws Exception {
        // given
        given(transactionService.getTransactedWastes(anyLong(), any(TransactionMemberType.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(WasteResponse.fromEntity(Fixture.createWaste()))));
        // when
        mvc.perform(get("/api/v1/transactions").queryParam("memberType", memberType.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(1));

        // then
    }
}