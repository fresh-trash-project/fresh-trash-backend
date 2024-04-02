package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.entity.audit.AuditingAt;
import freshtrash.freshtrashbackend.entity.constants.AccountStatus;
import freshtrash.freshtrashbackend.entity.constants.LoginType;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members")
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@TypeDef(name = "json", typeClass = JsonType.class)
public class Member extends AuditingAt implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private double rating;

    @Column(nullable = false)
    private String nickname;

    @Type(type = "json")
    @Column(columnDefinition = "longtext")
    private Address address;

    @Column
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus;

    public Member(
            Long id,
            String email,
            double rating,
            String nickname,
            String fileName,
            LoginType loginType,
            UserRole userRole,
            AccountStatus accountStatus,
            Address address) {
        this.id = id;
        this.email = email;
        this.rating = rating;
        this.nickname = nickname;
        this.fileName = fileName;
        this.loginType = loginType;
        this.userRole = userRole;
        this.accountStatus = accountStatus;
        this.address = address;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }
}
