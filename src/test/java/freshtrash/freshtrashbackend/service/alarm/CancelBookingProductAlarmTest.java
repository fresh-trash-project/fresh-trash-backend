package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.domain.alarm.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.domain.alarm.service.CancelBookingProductAlarm;
import freshtrash.freshtrashbackend.domain.alarm.service.parameter.ProductAlarmParameter;
import freshtrash.freshtrashbackend.domain.chatRoom.entity.ChatRoom;
import freshtrash.freshtrashbackend.domain.chatRoom.entity.constants.ChatRoomSellStatus;
import freshtrash.freshtrashbackend.domain.chatRoom.service.ChatRoomService;
import freshtrash.freshtrashbackend.domain.product.entity.constants.ProductSellStatus;
import freshtrash.freshtrashbackend.domain.product.service.ProductDealService;
import freshtrash.freshtrashbackend.producer.ProductDealProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
        ProductAlarmParameter productAlarmParameter = new ProductAlarmParameter(chatRoomId, memberId);
        ChatRoom chatRoom = Fixture.createChatRoom();
        given(chatRoomService.getChatRoom(chatRoomId, memberId)).willReturn(chatRoom);
        willDoNothing()
                .given(productDealService)
                .updateSellStatus(
                        chatRoom.getProductId(),
                        chatRoom.getId(),
                        ProductSellStatus.ONGOING,
                        ChatRoomSellStatus.ONGOING);
        ChatRoom otherChatRoom = Fixture.createChatRoom(
                chatRoom.getProductId(), chatRoom.getSellerId(), 123L, true, ChatRoomSellStatus.ONGOING);
        given(chatRoomService.getNotClosedChatRoomsByProductId(chatRoom.getProductId()))
                .willReturn(List.of(otherChatRoom));
        willDoNothing()
                .given(producer)
                .publishForUpdatedSellStatus(otherChatRoom, "🛒seller님이 판매중으로 변경하였습니다.", AlarmType.CANCEL_BOOKING);
        // when
        assertThatCode(() -> cancelBookingProductAlarm.sendAlarm(productAlarmParameter))
                .doesNotThrowAnyException();
        // then
    }

    @DisplayName("CANCEL_BOOKING 타입의 알람 전송을 수행하는 작업을 지원한다.")
    @Test
    void given_alarmType_when_supported_then_returnTrue() {
        // given
        AlarmType alarmType = AlarmType.CANCEL_BOOKING;
        // when
        boolean isSupport = cancelBookingProductAlarm.supports(alarmType);
        // then
        assertThat(isSupport).isTrue();
    }
}