package freshtrash.freshtrashbackend.entity;

import lombok.*;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AlarmArgs {
    private Long fromMemberId; // 알람을 발생시킨 유저
    private Long targetId; // 알람 대상 (waste, chat, ...)

    public static AlarmArgs of(Long fromMemberId, Long targetId) {
        return new AlarmArgs(fromMemberId, targetId);
    }

    public static AlarmArgs of(Long targetId) {
        return new AlarmArgs(null, targetId);
    }
}
