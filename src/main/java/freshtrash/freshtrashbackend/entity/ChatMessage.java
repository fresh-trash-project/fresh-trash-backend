package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.entity.audit.CreatedAt;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "chat_messages")
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChatMessage extends CreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "chatRoomId", insertable = false, updatable = false)
    private ChatRoom chatRoom;

    @Column(nullable = false)
    private Long chatRoomId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    private ChatMessage(Long chatRoomId, Member member, String message) {
        this.chatRoomId = chatRoomId;
        this.member = member;
        this.message = message;
    }

    public static ChatMessage of(Long chatRoomId, Member member, String message) {
        return new ChatMessage(chatRoomId, member, message);
    }
}
