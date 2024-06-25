package freshtrash.freshtrashbackend.service.producer;

import freshtrash.freshtrashbackend.dto.events.AlarmEvent;
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
class ChatProducerTest {
    @InjectMocks
    private ChatProducer chatProducer;

    @Mock
    private MQPublisher mqPublisher;

    @Test
    @DisplayName("다른 사용자에게 신고 당했다는 메시지를 전송한다.")
    void occurredUserFlag() {
        // given
        Long productId = 1L, targetMemberId = 3L, currentMemberId = 2L;
        String message = "message";
        willDoNothing().given(mqPublisher).publish(any(AlarmEvent.class));
        // when
        assertThatCode(() -> chatProducer.occurredUserFlag(productId, targetMemberId, currentMemberId, message))
                .doesNotThrowAnyException();
        // then
    }
}