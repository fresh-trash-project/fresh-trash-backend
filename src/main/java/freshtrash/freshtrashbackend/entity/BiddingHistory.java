package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.entity.audit.CreatedAt;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
@ToString(callSuper = true)
@Table(name = "bidding_history")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "update bidding_history set deleted_at = current_timestamp where id=?")
@Where(clause = "deleted_at is NULL")
public class BiddingHistory extends CreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private int price; // 입찰 금액

    @Setter
    @Column(nullable = false)
    private boolean isPay; // 결제 여부

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

    private LocalDateTime deletedAt;

    @Builder
    public BiddingHistory(int price, Long memberId, Long auctionId) {
        this.price = price;
        this.memberId = memberId;
        this.auctionId = auctionId;
    }
}
