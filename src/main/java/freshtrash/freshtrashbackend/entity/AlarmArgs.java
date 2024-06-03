package freshtrash.freshtrashbackend.entity;

import lombok.*;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmArgs {
    private Long fromMemberId; // 알람을 발생시킨 유저
    private Long targetId; // 알람 대상 (product, chat, ...)

    public static AlarmArgs of(Long fromMemberId, Long targetId) {
        return new AlarmArgs(fromMemberId, targetId);
    }

    public static AlarmArgs of(Long targetId) {
        return new AlarmArgs(null, targetId);
    }
}
