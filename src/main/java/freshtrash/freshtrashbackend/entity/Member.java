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
import java.util.LinkedHashSet;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

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

    @Column(nullable = false, unique = true)
    private String email;

    @Setter
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private double rating;

    @Column(nullable = false, unique = true)
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

    @ToString.Exclude
    @OneToMany(mappedBy = "member", cascade = ALL, fetch = LAZY)
    private Set<Waste> wastes = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "member", cascade = ALL, fetch = LAZY)
    private Set<Alarm> alarms = new LinkedHashSet<>();

    @PrePersist
    private void prePersist() {
        this.rating = 0;
    }

    private Member(
            String email,
            String password,
            String nickname,
            LoginType loginType,
            UserRole userRole,
            AccountStatus accountStatus) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.loginType = loginType;
        this.userRole = userRole;
        this.accountStatus = accountStatus;
    }

    public static Member of(
            String email,
            String password,
            String nickname,
            LoginType loginType,
            UserRole userRole,
            AccountStatus accountStatus) {
        return new Member(email, password, nickname, loginType, userRole, accountStatus);
    }

    @Override
    public boolean isNew() {
        return id == null;
    }
}
