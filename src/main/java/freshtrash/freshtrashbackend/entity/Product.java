package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.dto.request.ProductRequest;
import freshtrash.freshtrashbackend.entity.audit.AuditingAt;
import freshtrash.freshtrashbackend.entity.constants.ProductCategory;
import freshtrash.freshtrashbackend.entity.constants.ProductStatus;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
@Table(name = "products")
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@TypeDef(name = "json", typeClass = JsonType.class)
public class Product extends AuditingAt {
    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Setter
    @Column(nullable = false)
    private String title;

    @Setter
    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Setter
    @Column(nullable = false)
    private int productPrice;

    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = false)
    private int viewCount;

    @Setter
    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ProductCategory productCategory;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ProductStatus productStatus;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private SellStatus sellStatus;

    @Type(type = "json")
    @Column(columnDefinition = "longtext")
    private Address address;

    @Setter
    private LocalDateTime productDealAt;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = LAZY)
    @JoinColumn(name = "memberId", insertable = false, updatable = false)
    private Member member;

    @Column(nullable = false)
    private Long memberId;

    @Builder
    private Product(
            String title,
            String content,
            Integer productPrice,
            String fileName,
            ProductCategory productCategory,
            ProductStatus productStatus,
            SellStatus sellStatus,
            Address address,
            Long memberId) {
        this.title = title;
        this.content = content;
        this.productPrice = productPrice;
        this.fileName = fileName;
        this.productCategory = productCategory;
        this.productStatus = productStatus;
        this.sellStatus = sellStatus;
        this.address = address;
        this.memberId = memberId;
    }

    public static class ProductBuilder {
        public ProductBuilder address(Address address) {
            this.address = address.allBlank() ? null : address;
            return this;
        }
    }

    public static Product fromRequest(ProductRequest productRequest, String fileName, Long memberId) {
        return Product.builder()
                .title(productRequest.title())
                .content(productRequest.content())
                .productPrice(productRequest.productPrice())
                .fileName(fileName)
                .productCategory(productRequest.productCategory())
                .productStatus(productRequest.productStatus())
                .sellStatus(productRequest.sellStatus())
                .address(productRequest.address())
                .memberId(memberId)
                .build();
    }
}
