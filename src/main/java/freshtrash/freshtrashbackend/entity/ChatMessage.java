package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.entity.audit.AuditingAt;
import lombok.*;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Entity
@Table(name = "chat_message")
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChatMessage extends AuditingAt implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(nullable = false)
    private Long chatRoomId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", insertable = false, updatable = false)
    private Member member;

    @Column(nullable = false)
    private Long memberId;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Builder
    public ChatMessage(Long chatRoomId, Long memberId, String message) {
        this.chatRoomId = chatRoomId;
        this.memberId = memberId;
        this.message = message;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }
}
