package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.entity.audit.CreatedAt;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "product_likes",
        indexes = @Index(name = "member_id_and_product_id", columnList = "memberId, productId", unique = true))
public class ProductLike extends CreatedAt {
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
    @JoinColumn(name = "productId", insertable = false, updatable = false)
    private Product product;

    @Column(nullable = false)
    private Long productId;

    private ProductLike(Long memberId, Long productId) {
        this.memberId = memberId;
        this.productId = productId;
    }

    public static ProductLike of(Long memberId, Long productId) {
        return new ProductLike(memberId, productId);
    }
}
