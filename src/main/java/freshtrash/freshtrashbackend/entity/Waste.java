package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.entity.audit.AuditingAt;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.entity.constants.WasteCategory;
import freshtrash.freshtrashbackend.entity.constants.WasteStatus;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Getter
@ToString(callSuper = true)
@Table(name = "wastes")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@TypeDef(name = "json", typeClass = JsonType.class)
public class Waste extends AuditingAt {
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
    private int wastePrice;

    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = false)
    private int viewCount;

    @Setter
    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private WasteCategory wasteCategory;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private WasteStatus wasteStatus;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private SellStatus sellStatus;

    @Type(type = "json")
    @Column(columnDefinition = "longtext")
    private Address address;

    @Setter
    private LocalDateTime transactionAt;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = LAZY)
    @JoinColumn(name = "memberId", insertable = false, updatable = false)
    private Member member;

    @Column(nullable = false)
    private Long memberId;

    @Builder
    private Waste(
            String title,
            String content,
            Integer wastePrice,
            String fileName,
            WasteCategory wasteCategory,
            WasteStatus wasteStatus,
            SellStatus sellStatus,
            Address address,
            Long memberId) {
        this.title = title;
        this.content = content;
        this.wastePrice = wastePrice;
        this.fileName = fileName;
        this.wasteCategory = wasteCategory;
        this.wasteStatus = wasteStatus;
        this.sellStatus = sellStatus;
        this.address = address;
        this.memberId = memberId;
    }

    public static class WasteBuilder {
        public WasteBuilder address(Address address) {
            this.address = address.allBlank() ? null : address;
            return this;
        }
    }
}
