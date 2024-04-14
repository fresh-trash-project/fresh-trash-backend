package freshtrash.freshtrashbackend.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private boolean openOrClose;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wasteId", insertable = false, updatable = false)
    private Waste waste;

    @Column(nullable = false)
    private Long wasteId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sellerId", insertable = false, updatable = false)
    private Member seller;

    @Column(nullable = false)
    private Long sellerId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyerId", insertable = false, updatable = false)
    private Member buyer;

    @Column(nullable = false)
    private Long buyerId;

    @Builder
    public ChatRoom(Long wasteId, Long sellerId, Long buyerId, boolean openOrClose) {
        this.wasteId = wasteId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.openOrClose = openOrClose;
    }
}
