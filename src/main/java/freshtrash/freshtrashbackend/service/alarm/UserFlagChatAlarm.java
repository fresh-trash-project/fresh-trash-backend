package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.service.MemberService;
import freshtrash.freshtrashbackend.service.alarm.template.ChatAlarmTemplate;
import freshtrash.freshtrashbackend.service.producer.ChatProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static freshtrash.freshtrashbackend.dto.constants.AlarmMessage.EXCEED_FLAG_MESSAGE;
import static freshtrash.freshtrashbackend.dto.constants.AlarmMessage.FLAG_MESSAGE;

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
}
