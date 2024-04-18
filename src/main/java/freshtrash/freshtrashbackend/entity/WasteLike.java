package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.entity.audit.CreatedAt;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@Table(
        name = "waste_likes",
        indexes = @Index(name = "member_id_and_waste_id", columnList = "memberId, wasteId", unique = true))
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WasteLike extends CreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", insertable = false, updatable = false)
    private Member member;

    @Column(nullable = false)
    private Long memberId;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "wasteId", insertable = false, updatable = false)
    private Waste waste;

    @Column(nullable = false)
    private Long wasteId;

    private WasteLike(Long memberId, Long wasteId) {
        this.memberId = memberId;
        this.wasteId = wasteId;
    }

    public static WasteLike of(Long memberId, Long wasteId) {
        return new WasteLike(memberId, wasteId);
    }
}
