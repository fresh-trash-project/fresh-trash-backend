package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.ProductDealService;
import freshtrash.freshtrashbackend.service.alarm.parameter.ProductAlarmParameter;
import freshtrash.freshtrashbackend.service.producer.ProductDealProducer;
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
class RequestBookingProductAlarmTest {
    @InjectMocks
    private RequestBookingProductAlarm requestBookingProductAlarm;

    @Mock
    private ProductDealService productDealService;

    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private ProductDealProducer producer;

    @Test
    @DisplayName("예약 요청을 하면 SellStatus를 업데이트라고 알림을 전송한다.")
    void given_chatRomIdAndMemberId_when_requestBooking_then_updateSellStatusAndSendAlarm() {
        // given
        Long chatRomId = 1L, memberId = 2L;
        ProductAlarmParameter productAlarmParameter = new ProductAlarmParameter(chatRomId, memberId);
        ChatRoom chatRoom = Fixture.createChatRoom();
        given(chatRoomService.getChatRoom(chatRomId, memberId)).willReturn(chatRoom);
        willDoNothing()
                .given(productDealService)
                .updateSellStatus(chatRoom.getProductId(), chatRoom.getId(), SellStatus.BOOKING);
        ChatRoom otherChatRoom =
                Fixture.createChatRoom(chatRoom.getProductId(), chatRoom.getSellerId(), 123L, true, SellStatus.ONGOING);
        given(chatRoomService.getNotClosedChatRoomsByProductId(chatRoom.getProductId()))
                .willReturn(List.of(otherChatRoom));
        willDoNothing()
                .given(producer)
                .publishForUpdatedSellStatus(otherChatRoom, "🛒seller님이 예약중으로 변경하였습니다.", AlarmType.REQUEST_BOOKING);
        // when
        assertThatCode(() -> requestBookingProductAlarm.sendAlarm(productAlarmParameter))
                .doesNotThrowAnyException();
        // then
    }

    @DisplayName("REQUEST_BOOKING 타입의 알람 전송을 수행하는 작업을 지원한다.")
    @Test
    void given_alarmType_when_supported_then_returnTrue() {
        //given
        AlarmType alarmType = AlarmType.REQUEST_BOOKING;
        //when
        boolean isSupport = requestBookingProductAlarm.supports(alarmType);
        //then
        assertThat(isSupport).isTrue();
    }
}