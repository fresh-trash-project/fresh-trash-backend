package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.entity.audit.AuditingAt;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.entity.constants.WasteCategory;
import freshtrash.freshtrashbackend.entity.constants.WasteStatus;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import static javax.persistence.FetchType.LAZY;

@Getter
@ToString(callSuper = true)
@Table(name = "wastes")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@TypeDef(name = "json", typeClass = JsonType.class)
public class Waste extends AuditingAt implements Persistable<Long> {
    @Id
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

    @Setter
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private WasteCategory wasteCategory;

    @Setter
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private WasteStatus wasteStatus;

    @Setter
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private SellStatus sellStatus;

    @Setter
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

    // TODO: waste_reviews와 연관 관계 설정
    // TODO: chat_room과 연관 관계 설정
    // TODO: transaction_logs와 연관 관계 설정
    // TODO: waste_likes와 연관 관계 설정

    @PrePersist
    private void prePersist() {
        this.likeCount = 0;
        this.viewCount = 0;
    }

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

    @Override
    public boolean isNew() {
        return Objects.isNull(this.id);
    }
}
