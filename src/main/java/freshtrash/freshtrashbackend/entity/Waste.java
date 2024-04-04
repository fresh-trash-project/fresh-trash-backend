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

    // TODO: waste_reviews와 연관 관계 설정
    // TODO: chat_room과 연관 관계 설정
    // TODO: transaction_logs와 연관 관계 설정
    // TODO: waste_likes와 연관 관계 설정
    // TODO: members와 연관 관계 설정

    @PrePersist
    private void prePersist() {
        this.likeCount = Objects.isNull(this.likeCount) ? 0 : this.likeCount;
        this.viewCount = Objects.isNull(this.viewCount) ? 0 : this.viewCount;
    }

    private Waste(
            String title,
            String content,
            Integer wastePrice,
            String fileName,
            WasteCategory wasteCategory,
            WasteStatus wasteStatus,
            SellStatus sellStatus,
            Address address) {
        this.title = title;
        this.content = content;
        this.wastePrice = wastePrice;
        this.fileName = fileName;
        this.wasteCategory = wasteCategory;
        this.wasteStatus = wasteStatus;
        this.sellStatus = sellStatus;
        this.address = address;
    }

    public static Waste of(
            String title,
            String content,
            Integer wastePrice,
            String fileName,
            WasteCategory wasteCategory,
            WasteStatus wasteStatus,
            SellStatus sellStatus,
            Address address) {
        return new Waste(title, content, wastePrice, fileName, wasteCategory, wasteStatus, sellStatus, address);
    }

    @Override
    public boolean isNew() {
        return Objects.isNull(this.id);
    }
}
