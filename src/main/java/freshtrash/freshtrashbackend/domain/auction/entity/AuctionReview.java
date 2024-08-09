package freshtrash.freshtrashbackend.domain.auction.entity;

import freshtrash.freshtrashbackend.domain.auction.dto.request.AuctionReviewRequest;
import freshtrash.freshtrashbackend.domain.member.entity.Member;
import freshtrash.freshtrashbackend.global.common.audit.CreatedAt;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
@ToString(callSuper = true)
@Table(name = "auction_reviews")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuctionReview extends CreatedAt {
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
    @OneToOne(optional = false, fetch = LAZY)
    @JoinColumn(name = "auctionId", insertable = false, updatable = false)
    private Auction auction;

    @Column(nullable = false)
    private Long auctionId;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "text")
    private String content;

    @Builder
    public AuctionReview(Long memberId, Long auctionId, int rating, String content) {
        this.memberId = memberId;
        this.auctionId = auctionId;
        this.rating = rating;
        this.content = content;
    }

    public static AuctionReview fromRequest(AuctionReviewRequest auctionReviewRequest, Long auctionId, Long memberId) {
        return AuctionReview.builder()
                .content(auctionReviewRequest.content())
                .rating(auctionReviewRequest.rate())
                .memberId(memberId)
                .auctionId(auctionId)
                .build();
    }
}
