package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.entity.audit.CreatedAt;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@ToString(callSuper = true)
@Table(name = "transaction_logs")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionLog extends CreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "wasteId", insertable = false, updatable = false)
    private Waste waste;

    @Column(nullable = false)
    private Long wasteId;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sellerId", insertable = false, updatable = false)
    private Member seller;

    @Column(nullable = false)
    private Long sellerId;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "buyerId", insertable = false, updatable = false)
    private Member buyer;

    @Column(nullable = false)
    private Long buyerId;

    @Builder
    private TransactionLog(Long wasteId, Long sellerId, Long buyerId) {
        this.wasteId = wasteId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
    }
}
