package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.dto.request.ReviewRequest;
import freshtrash.freshtrashbackend.entity.audit.CreatedAt;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@ToString(callSuper = true)
@Table(name = "waste_reviews")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class WasteReview extends CreatedAt {
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

    @Builder
    private WasteReview(Long memberId, Long wasteId, int rating) {
        this.memberId = memberId;
        this.wasteId = wasteId;
        this.rating = rating;
    }

    public static WasteReview fromRequest(ReviewRequest reviewRequest, Long wasteId, Long memberId) {
        return WasteReview.builder()
                .rating(reviewRequest.rate())
                .memberId(memberId)
                .wasteId(wasteId)
                .build();
    }
}
