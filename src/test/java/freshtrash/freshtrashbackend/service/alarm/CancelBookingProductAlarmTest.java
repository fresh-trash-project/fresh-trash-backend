package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CancelBookingProductAlarmTest {
    @InjectMocks
    private CancelBookingProductAlarm cancelBookingProductAlarm;

    @Mock
    private ProductDealService productDealService;

    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private ProductDealProducer producer;

    @Test
    @DisplayName("예약이 취소되었을 경우 중고 상품의 거래 상태를 변경하고 채팅을 요청한 모든 사용자에게 알림을 전송한다.")
    void given_chatRoomIdAndMemberId_when_cancelBooking_then_updateSellStatusAndSendAlarmToAllChatUsers() {
        // given
        Long chatRoomId = 1L, memberId = 2L;
        ChatRoom chatRoom = Fixture.createChatRoom();
        given(chatRoomService.getChatRoom(chatRoomId, memberId)).willReturn(chatRoom);
        willDoNothing()
                .given(productDealService)
                .updateSellStatus(chatRoom.getProductId(), chatRoom.getId(), SellStatus.ONGOING);
        ChatRoom otherChatRoom =
                Fixture.createChatRoom(chatRoom.getProductId(), chatRoom.getSellerId(), 123L, true, SellStatus.ONGOING);
        given(chatRoomService.getNotClosedChatRoomsByProductId(chatRoom.getProductId()))
                .willReturn(List.of(otherChatRoom));
        willDoNothing()
                .given(producer)
                .publishForUpdatedSellStatus(otherChatRoom, "seller님이 판매중으로 판매상태를 변경하였습니다.", AlarmType.TRANSACTION);
        // when
        assertThatCode(() -> cancelBookingProductAlarm.sendAlarm(chatRoomId, memberId))
                .doesNotThrowAnyException();
        // then
    }
}