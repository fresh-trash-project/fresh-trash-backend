package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.entity.audit.CreatedAt;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@ToString(callSuper = true)
@Table(name = "productDeal_logs")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDealLog extends CreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", insertable = false, updatable = false)
    private Product product;

    @Column(nullable = false)
    private Long productId;

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
    private ProductDealLog(Long productId, Long sellerId, Long buyerId) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
    }
}
