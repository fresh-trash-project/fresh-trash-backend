package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.entity.constants.UserRole;
import freshtrash.freshtrashbackend.service.MemberService;
import freshtrash.freshtrashbackend.service.producer.ChatProducer;
import org.springframework.stereotype.Component;

import static freshtrash.freshtrashbackend.dto.constants.AlarmMessage.EXCEED_FLAG_MESSAGE;
import static freshtrash.freshtrashbackend.dto.constants.AlarmMessage.FLAG_MESSAGE;

@Component
public class UserFlagChatAlarm extends ChatAlarmTemplate {

    private static int FLAG_LIMIT = 10;

    public UserFlagChatAlarm(MemberService memberService, ChatProducer producer) {
        super(memberService, producer);
    }

    /**
     * 유저 신고 횟수 + 1
     */
    @Override
    int update(Long targetMemberId) {
        int flagCount = this.memberService.updateFlagCount(targetMemberId).flagCount();
        if (flagCount >= FLAG_LIMIT) this.memberService.updateMemberRole(targetMemberId, UserRole.BLACK_USER);
        return flagCount;
    }

    @Override
    void publishEvent(int flagCount, Long wasteId, Long targetMemberId, Long currentMemberId) {
        String message = generateMessage(flagCount);
        this.producer.occurredUserFlag(wasteId, targetMemberId, currentMemberId, message);
    }

    private String generateMessage(int flagCount) {
        return flagCount >= FLAG_LIMIT
                ? EXCEED_FLAG_MESSAGE.getMessage()
                : String.format(FLAG_MESSAGE.getMessage(), flagCount);
    }
}
