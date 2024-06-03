package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.dto.request.ReviewRequest;
import freshtrash.freshtrashbackend.entity.audit.CreatedAt;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
@ToString(callSuper = true)
@Table(name = "product_reviews")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductReview extends CreatedAt {
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
    @JoinColumn(name = "productId", insertable = false, updatable = false)
    private Product product;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int rating;

    @Builder
    private ProductReview(Long memberId, Long productId, int rating) {
        this.memberId = memberId;
        this.productId = productId;
        this.rating = rating;
    }

    public static ProductReview fromRequest(ReviewRequest reviewRequest, Long productId, Long memberId) {
        return ProductReview.builder()
                .rating(reviewRequest.rate())
                .memberId(memberId)
                .productId(productId)
                .build();
    }
}
