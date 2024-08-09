package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.domain.alarm.service.UserFlagChatAlarm;
import freshtrash.freshtrashbackend.domain.alarm.service.parameter.ChatAlarmParameter;
import freshtrash.freshtrashbackend.domain.member.dto.projections.FlagCountSummary;
import freshtrash.freshtrashbackend.domain.chatRoom.entity.ChatRoom;
import freshtrash.freshtrashbackend.domain.member.entity.Member;
import freshtrash.freshtrashbackend.domain.alarm.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.domain.member.service.MemberService;
import freshtrash.freshtrashbackend.producer.ChatProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
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
        ChatAlarmParameter chatAlarmParameter = new ChatAlarmParameter(chatRoom, memberId);
        given(memberService.updateFlagCount(chatRoom.getSellerId(), Member.USER_FLAG_LIMIT))
                .willReturn(new FlagCountSummary(3));
        willDoNothing()
                .given(producer)
                .occurredUserFlag(
                        chatRoom.getProductId(),
                        chatRoom.getSellerId(),
                        chatRoom.getBuyerId(),
                        "🚩3번 경고를 받으셨습니다. 경고가 10번 누적되면 서비스를 이용하실 수 없습니다.");
        // when
        assertThatCode(() -> userFlagChatAlarm.sendAlarm(chatAlarmParameter)).doesNotThrowAnyException();
        // then
    }

    @DisplayName("FLAG 타입의 알람 전송을 수행하는 작업을 지원한다.")
    @Test
    void given_alarmType_when_supported_then_returnTrue() {
        //given
        AlarmType alarmType = AlarmType.FLAG;
        //when
        boolean isSupport = userFlagChatAlarm.supports(alarmType);
        //then
        assertThat(isSupport).isTrue();
    }
}