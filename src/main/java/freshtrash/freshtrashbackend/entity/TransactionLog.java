package freshtrash.freshtrashbackend.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@ToString
@Table(name = "transaction_logs")
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private int price;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

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
    private TransactionLog(int price, Long wasteId, Long sellerId, Long buyerId) {
        this.price = price;
        this.wasteId = wasteId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
    }
}
