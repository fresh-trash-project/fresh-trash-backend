package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.entity.audit.CreatedAt;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
@ToString(callSuper = true)
@Table(name = "bidding_history")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BiddingHistory extends CreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private int price; // 입찰 금액

    @Column(nullable = false)
    private boolean isSuccessBidding; // 낙찰 여부

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = LAZY)
    @JoinColumn(name = "memberId", insertable = false, updatable = false)
    private Member member;

    @Column(nullable = false)
    private Long memberId;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = LAZY)
    @JoinColumn(name = "auctionId", insertable = false, updatable = false)
    private Auction auction;

    @Column(nullable = false)
    private Long auctionId;

    @Builder
    public BiddingHistory(int price, Long memberId, Long auctionId) {
        this.price = price;
        this.memberId = memberId;
        this.auctionId = auctionId;
    }
}
