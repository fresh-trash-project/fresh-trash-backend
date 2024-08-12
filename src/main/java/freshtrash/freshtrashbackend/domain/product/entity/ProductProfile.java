package freshtrash.freshtrashbackend.domain.product.entity;

import freshtrash.freshtrashbackend.global.common.audit.AuditingAt;
import lombok.*;
import org.hibernate.type.descriptor.sql.LongVarbinaryTypeDescriptor;

import javax.persistence.*;
import java.sql.Blob;

@Getter
@Entity
@Table(name = "product_profiles")
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductProfile extends AuditingAt {
    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(length = 16777215)
    private byte[] profile; // 카테고리, 제목, 본문의 feature(vector)

    @ToString.Exclude
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", insertable = false, updatable = false)
    private Product product;

    @Column(nullable = false)
    private Long productId;

    private ProductProfile(Long productId) {
        this.productId = productId;
    }

    public static ProductProfile of(Long productId) {
        return new ProductProfile(productId);
    }

}
