package freshtrash.freshtrashbackend.domain.alarm.service.adapter;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.domain.alarm.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.domain.alarm.service.parameter.AuctionAlarmParameter;
import freshtrash.freshtrashbackend.domain.alarm.service.parameter.BiddingHistoryAlarmParameter;
import freshtrash.freshtrashbackend.domain.alarm.service.parameter.ChatAlarmParameter;
import freshtrash.freshtrashbackend.domain.alarm.service.parameter.ProductAlarmParameter;
import freshtrash.freshtrashbackend.domain.alarm.service.template.AuctionAlarmTemplate;
import freshtrash.freshtrashbackend.domain.alarm.service.template.BiddingHistoryAlarmTemplate;
import freshtrash.freshtrashbackend.domain.alarm.service.template.ChatAlarmTemplate;
import freshtrash.freshtrashbackend.domain.alarm.service.template.ProductAlarmTemplate;
import freshtrash.freshtrashbackend.domain.auction.entity.Auction;
import freshtrash.freshtrashbackend.domain.auction.entity.BiddingHistory;
import freshtrash.freshtrashbackend.domain.chatRoom.entity.ChatRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AlarmMappingHandlerAdapterTest {
    @InjectMocks
    private AlarmMappingHandlerAdapter alarmMappingHandlerAdapter;

    @Mock
    private ApplicationContext context;

    @Mock
    private AuctionAlarmTemplate auctionAlarmTemplate;

    @Mock
    private BiddingHistoryAlarmTemplate biddingHistoryAlarmTemplate;

    @Mock
    private ChatAlarmTemplate chatAlarmTemplate;

    @Mock
    private ProductAlarmTemplate productAlarmTemplate;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                this.alarmMappingHandlerAdapter,
                "handlerMappings",
                List.of(chatAlarmTemplate, auctionAlarmTemplate, productAlarmTemplate, biddingHistoryAlarmTemplate));
    }

    @DisplayName("채팅관련 알림 프로세스를 실행한다.")
    @Test
    void chatAlarmHandle() {
        // given
        ChatRoom chatRoom = Fixture.createChatRoom();
        Long memberId = 123L;
        AlarmType alarmType = AlarmType.FLAG;
        given(chatAlarmTemplate.supports(alarmType)).willReturn(true);
        willDoNothing().given(chatAlarmTemplate).sendAlarm(any(ChatAlarmParameter.class));
        // when
        assertThatCode(() -> alarmMappingHandlerAdapter.handle(chatRoom, memberId, alarmType))
                .doesNotThrowAnyException();
        // then
    }

    @DisplayName("경매관련 알림 프로세스를 실행한다.")
    @Test
    void auctionAlarmHandle() {
        // given
        Auction auction = Fixture.createAuction();
        AlarmType alarmType = AlarmType.CANCEL_AUCTION;
        given(auctionAlarmTemplate.supports(alarmType)).willReturn(true);
        willDoNothing().given(auctionAlarmTemplate).sendAlarm(any(AuctionAlarmParameter.class));
        // when
        assertThatCode(() -> alarmMappingHandlerAdapter.handle(auction, alarmType))
                .doesNotThrowAnyException();
        // then
    }

    @DisplayName("입찰관련 알림 프로세스를 실행한다.")
    @Test
    void biddingAlarmHandle() {
        // given
        BiddingHistory biddingHistory = Fixture.createBiddingHistory(1L, 123L, 1000);
        AlarmType alarmType = AlarmType.BIDDING;
        given(biddingHistoryAlarmTemplate.supports(alarmType)).willReturn(true);
        willDoNothing().given(biddingHistoryAlarmTemplate).sendAlarm(any(BiddingHistoryAlarmParameter.class));
        // when
        assertThatCode(() -> alarmMappingHandlerAdapter.handle(biddingHistory, alarmType))
                .doesNotThrowAnyException();
        // then
    }

    @DisplayName("중고 거래관련 알림 프로세스를 실행한다.")
    @Test
    void ProductAlarmHandle() {
        // given
        Long chatRoomId = 1L;
        Long memberId = 123L;
        AlarmType alarmType = AlarmType.CANCEL_BOOKING;
        given(productAlarmTemplate.supports(alarmType)).willReturn(true);
        willDoNothing().given(productAlarmTemplate).sendAlarm(any(ProductAlarmParameter.class));
        // when
        assertThatCode(() -> alarmMappingHandlerAdapter.handle(chatRoomId, memberId, alarmType))
                .doesNotThrowAnyException();
        // then
    }
}
