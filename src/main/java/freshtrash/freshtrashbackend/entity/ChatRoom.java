package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.entity.audit.AuditingAt;
import lombok.*;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Entity
@Table(name = "chat_room")
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChatRoom extends AuditingAt implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waste_id", nullable = false)
    private Waste waste;

    @Column(nullable = false)
    private Long wasteId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "open_or_close", nullable = false)
    private boolean openOrClose;

    @Builder
    public ChatRoom(Long wasteId, Long sellerId, Long buyerId, boolean openOrClose) {
        this.wasteId = wasteId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.openOrClose = openOrClose;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }
}

