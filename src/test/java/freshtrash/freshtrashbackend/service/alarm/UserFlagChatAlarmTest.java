package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.dto.projections.FlagCountSummary;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.service.MemberService;
import freshtrash.freshtrashbackend.service.producer.ChatProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserFlagChatAlarmTest {
    @InjectMocks
    private UserFlagChatAlarm userFlagChatAlarm;

    @Mock
    private MemberService memberService;

    @Mock
    private ChatProducer producer;

    @Test
    @DisplayName("다른 사용자에게 신고 당하면 flagCount + 1 업데이트 후 알림을 전송한다.")
    void given_chatRoomAndMemberId_when_reportFromOtherUser_then_updateFlagCountAndSendAlarm() {
        // given
        ChatRoom chatRoom = Fixture.createChatRoom();
        Long memberId = 2L;
        given(memberService.updateFlagCount(chatRoom.getSellerId(), Member.USER_FLAG_LIMIT))
                .willReturn(new FlagCountSummary(3));
        willDoNothing()
                .given(producer)
                .occurredUserFlag(
                        chatRoom.getProductId(),
                        chatRoom.getSellerId(),
                        chatRoom.getBuyerId(),
                        "3번 신고받은 내역이 있습니다. 신고받은 횟수가 10번이상 되면 서비스를 이용하실 수 없습니다.");
        // when
        assertThatCode(() -> userFlagChatAlarm.sendAlarm(chatRoom, memberId)).doesNotThrowAnyException();
        // then
    }
}