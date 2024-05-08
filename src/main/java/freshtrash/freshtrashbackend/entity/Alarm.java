package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.dto.request.MessageRequest;
import freshtrash.freshtrashbackend.entity.audit.CreatedAt;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
@Table(name = "alarms")
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@TypeDef(name = "json", typeClass = JsonType.class)
public class Alarm extends CreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AlarmType alarmType;

    @Column(nullable = false, columnDefinition = "longtext")
    @Type(type = "json")
    private AlarmArgs alarmArgs;

    @Column(nullable = false)
    private String message;

    @Setter
    private LocalDateTime readAt;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = LAZY)
    @JoinColumn(name = "memberId", insertable = false, updatable = false)
    private Member member; // 알람을 받는 유저

    @Column(nullable = false)
    private Long memberId;

    @Builder
    private Alarm(AlarmType alarmType, AlarmArgs alarmArgs, String message, Long memberId) {
        this.alarmType = alarmType;
        this.alarmArgs = alarmArgs;
        this.message = message;
        this.memberId = memberId;
    }

    public static Alarm fromMessageRequest(MessageRequest messageRequest) {
        return Alarm.builder()
                .message(messageRequest.message())
                .memberId(messageRequest.memberId())
                .alarmType(messageRequest.alarmType())
                .alarmArgs(AlarmArgs.of(messageRequest.fromMemberId(), messageRequest.wasteId()))
                .build();
    }
}
