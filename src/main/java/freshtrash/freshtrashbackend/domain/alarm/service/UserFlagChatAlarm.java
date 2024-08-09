package freshtrash.freshtrashbackend.domain.alarm.service;

import freshtrash.freshtrashbackend.domain.member.entity.Member;
import freshtrash.freshtrashbackend.domain.alarm.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.domain.member.service.MemberService;
import freshtrash.freshtrashbackend.domain.alarm.service.template.ChatAlarmTemplate;
import freshtrash.freshtrashbackend.producer.ChatProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static freshtrash.freshtrashbackend.domain.alarm.dto.constants.AlarmMessage.EXCEED_FLAG_MESSAGE;
import static freshtrash.freshtrashbackend.domain.alarm.dto.constants.AlarmMessage.FLAG_MESSAGE;

@Slf4j
@Component
public class UserFlagChatAlarm extends ChatAlarmTemplate {

    public UserFlagChatAlarm(MemberService memberService, ChatProducer producer) {
        super(memberService, producer);
    }

    /**
     * 유저 신고 횟수 + 1
     */
    @Override
    public int update(Long targetMemberId) {
        log.debug("유저 신고 횟수 + 1 업데이트...");
        return this.memberService
                .updateFlagCount(targetMemberId, Member.USER_FLAG_LIMIT)
                .flagCount();
    }

    @Override
    public void publishEvent(int flagCount, Long productId, Long targetMemberId, Long currentMemberId) {
        log.debug("현재 신고 횟수 {}", flagCount);
        String message = generateMessage(flagCount);
        this.producer.occurredUserFlag(productId, targetMemberId, currentMemberId, message);
    }

    private String generateMessage(int flagCount) {
        return flagCount >= Member.USER_FLAG_LIMIT
                ? EXCEED_FLAG_MESSAGE.getMessage()
                : String.format(FLAG_MESSAGE.getMessage(), flagCount);
    }

    @Override
    public boolean supports(AlarmType alarmType) {
        return alarmType == AlarmType.FLAG;
    }
}
