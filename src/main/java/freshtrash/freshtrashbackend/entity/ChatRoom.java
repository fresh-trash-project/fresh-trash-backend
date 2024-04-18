package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.entity.audit.CreatedAt;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import lombok.*;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(
        name = "chat_rooms",
        indexes =
                @Index(
                        name = "waste_id_and_seller_id_and_buyer_id",
                        columnList = "wasteId, sellerId, buyerId",
                        unique = true))
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChatRoom extends CreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private SellStatus sellStatus;

    @Column(nullable = false)
    private boolean openOrClose;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "wasteId")
    private Waste waste;

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

    @ToString.Exclude
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private Set<ChatMessage> chatMessages = new LinkedHashSet<>();

    @Builder
    private ChatRoom(Waste waste, Long sellerId, Long buyerId, SellStatus sellStatus, boolean openOrClose) {
        this.waste = waste;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.sellStatus = sellStatus;
        this.openOrClose = openOrClose;
    }
}
