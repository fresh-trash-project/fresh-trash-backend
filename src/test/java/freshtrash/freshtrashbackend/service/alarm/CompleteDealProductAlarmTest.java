package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.ProductDealService;
import freshtrash.freshtrashbackend.service.producer.ProductDealProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CompleteDealProductAlarmTest {
    @InjectMocks
    private CompleteDealProductAlarm completeDealProductAlarm;

    @Mock
    private ProductDealService productDealService;

    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private ProductDealProducer producer;

    @Test
    @DisplayName("중고 거래가 완료되면 거래 상태를 CLOSE로 변경하고 거래 내역을 저장한 후 판매자, 구매자에게 알림을 전송한다.")
    void given_chatRoomIdAndMemberId_when_completeDeal_then_updateSellStatusAndSaveLogAndSendAlarmToSellerAndBuyer() {
        // given
        Long chatRomId = 1L, memberId = 2L;
        ChatRoom chatRoom = Fixture.createChatRoom();
        given(chatRoomService.getChatRoom(chatRomId, memberId)).willReturn(chatRoom);
        willDoNothing()
                .given(productDealService)
                .completeProductDeal(
                        chatRoom.getProductId(),
                        chatRoom.getId(),
                        chatRoom.getSellerId(),
                        chatRoom.getBuyerId(),
                        SellStatus.CLOSE);
        willDoNothing().given(producer).publishForCompletedProductDeal(chatRoom);
        willDoNothing().given(producer).publishToBuyerForRequestReview(chatRoom);
        ChatRoom otherChatRoom =
                Fixture.createChatRoom(chatRoom.getProductId(), chatRoom.getSellerId(), 123L, true, SellStatus.ONGOING);
        given(chatRoomService.getNotClosedChatRoomsByProductId(chatRoom.getProductId()))
                .willReturn(List.of(otherChatRoom));
        // when
        assertThatCode(() -> completeDealProductAlarm.sendAlarm(chatRomId, memberId))
                .doesNotThrowAnyException();
        // then
        then(producer).should(times(2)).publishForCompletedProductDeal(any(ChatRoom.class));
    }
}