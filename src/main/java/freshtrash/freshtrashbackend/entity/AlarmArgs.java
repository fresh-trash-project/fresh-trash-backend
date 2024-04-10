package freshtrash.freshtrashbackend.entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmArgs {
    private Long fromMemberId; // 알람을 발생시킨 유저
    private Long targetId; // 알람 대상 (waste, chat, ...)
}
