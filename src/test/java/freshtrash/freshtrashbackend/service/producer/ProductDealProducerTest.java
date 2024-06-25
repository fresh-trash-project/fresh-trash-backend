package freshtrash.freshtrashbackend.service.producer;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.dto.events.AlarmEvent;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.service.producer.publisher.MQPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ProductDealProducerTest {
    @InjectMocks
    private ProductDealProducer productDealProducer;

    @Mock
    private MQPublisher mqPublisher;

    @Test
    @DisplayName("중고 거래가 완료되었음을 알리는 메시지를 전송한다.")
    void publishForCompletedProductDeal() {
        // given
        ChatRoom chatRoom = Fixture.createChatRoom();
        publishAlarmEvent();
        // when
        assertThatCode(() -> productDealProducer.publishForCompletedProductDeal(chatRoom))
                .doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("구매자에게 리뷰를 요청하는 메시지를 전송한다.")
    void publishToBuyerForRequestReview() {
        // given
        ChatRoom chatRoom = Fixture.createChatRoom();
        publishAlarmEvent();
        // when
        assertThatCode(() -> productDealProducer.publishToBuyerForRequestReview(chatRoom))
                .doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("중고 상품의 판매 상태가 변경되었다는 것을 알리는 메시지를 전송한다.")
    void publishForUpdatedSellStatus() {
        // given
        ChatRoom chatRoom = Fixture.createChatRoom();
        String message = "message";
        AlarmType alarmType = AlarmType.TRANSACTION;
        publishAlarmEvent();
        // when
        assertThatCode(() -> productDealProducer.publishForUpdatedSellStatus(chatRoom, message, alarmType))
                .doesNotThrowAnyException();
        // then
    }

    private void publishAlarmEvent() {
        willDoNothing().given(mqPublisher).publish(any(AlarmEvent.class));
    }
}