package freshtrash.freshtrashbackend.domain.member.entity;

import freshtrash.freshtrashbackend.global.common.audit.AuditingAt;
import lombok.*;
import org.hibernate.type.descriptor.sql.LongVarbinaryTypeDescriptor;

import javax.persistence.*;
import java.sql.Blob;

@Getter
@Entity
@Table(name = "member_purchase_profiles")
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPurchaseProfile extends AuditingAt {
    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(length = 16777215)
    private byte[] productCumulativeSum; // Product Profile 벡터 누적 합

    @Column
    private int purchaseCount;

    @ToString.Exclude
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", insertable = false, updatable = false)
    private Member member;

    @Column(nullable = false)
    private Long memberId;

    private MemberPurchaseProfile(Long memberId) {
        this.memberId = memberId;
    }

    public static MemberPurchaseProfile of(Long memberId) {
        return new MemberPurchaseProfile(memberId);
    }
}
