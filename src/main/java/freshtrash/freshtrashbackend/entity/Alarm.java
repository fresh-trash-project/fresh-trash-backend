package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.dto.response.AlarmResponse;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import static javax.persistence.FetchType.LAZY;

@Getter
@ToString
@Table(name = "alarms")
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@TypeDef(name = "json", typeClass = JsonType.class)
public class Alarm implements Persistable<Long> {
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

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = LAZY)
    @JoinColumn(name = "memberId", insertable = false, updatable = false)
    private Member member; // 알람을 받는 유저

    @Column(nullable = false)
    private Long memberId;

    @Builder
    public Alarm(AlarmType alarmType, AlarmArgs alarmArgs, String message, Long memberId) {
        this.alarmType = alarmType;
        this.alarmArgs = alarmArgs;
        this.message = message;
        this.memberId = memberId;
    }

    @Override
    public boolean isNew() {
        return Objects.isNull(this.id);
    }

    public AlarmResponse toResponse() {
        return AlarmResponse.of(this.id, this.alarmType, this.alarmArgs, this.message);
    }
}
