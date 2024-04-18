package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.service.ChatService;
import freshtrash.freshtrashbackend.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        Long sellerId = 1L;
        Long buyerId = 2L;
        Long notBuyerId = 3L;
        given(chatService.getChatRoomsByWasteId(anyLong()))
                .willReturn(List.of(
                        Fixture.createChatRoom(wasteId, sellerId, buyerId, true, SellStatus.CLOSE),
                        Fixture.createChatRoom(wasteId, sellerId, notBuyerId, true, SellStatus.ONGOING)));
        willDoNothing().given(transactionService).saveTransactionLog(anyLong(), anyLong(), anyLong());
        willDoNothing().given(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Message.class));
        // when
        mvc.perform(post("/api/v1/transactions/" + wasteId)).andExpect(status().isOk());
        // then
    }
}