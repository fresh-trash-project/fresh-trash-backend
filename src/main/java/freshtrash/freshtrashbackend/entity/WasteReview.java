package freshtrash.freshtrashbackend.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Getter
@ToString
@Table(name = "waste_reviews")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class WasteReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = LAZY)
    @JoinColumn(name = "memberId", insertable = false, updatable = false)
    private Member member;

    @Column(nullable = false)
    private Long memberId;

    @ToString.Exclude
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "wasteId", insertable = false, updatable = false)
    private Waste waste;

    @Column(nullable = false)
    private Long wasteId;

    @Column(nullable = false)
    private int rating;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @Builder
    private WasteReview(Long memberId, Long wasteId, int rating) {
        this.memberId = memberId;
        this.wasteId = wasteId;
        this.rating = rating;
    }
}
