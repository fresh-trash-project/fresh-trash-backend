package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.dto.request.SignUpRequest;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.audit.AuditingAt;
import freshtrash.freshtrashbackend.entity.constants.AccountStatus;
import freshtrash.freshtrashbackend.entity.constants.LoginType;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "members")
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@TypeDef(name = "json", typeClass = JsonType.class)
public class Member extends AuditingAt {
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

    @Setter
    @Column(nullable = false, unique = true)
    private String nickname;

    @Setter
    @Type(type = "json")
    @Column(columnDefinition = "longtext")
    private Address address;

    @Setter
    @Column
    private String fileName;

    @Column
    private int flagCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus;

    @Builder
    private Member(
            String email,
            String password,
            String nickname,
            Address address,
            String fileName,
            LoginType loginType,
            UserRole userRole,
            AccountStatus accountStatus) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.address = address;
        this.fileName = fileName;
        this.loginType = loginType;
        this.userRole = userRole;
        this.accountStatus = accountStatus;
    }

    public static Member fromSignUpRequest(SignUpRequest signUpRequest) {
        return Member.builder()
                .email(signUpRequest.email())
                .password(signUpRequest.password())
                .nickname(signUpRequest.nickname())
                .loginType(LoginType.EMAIL)
                .userRole(UserRole.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }

    public static Member fromPrincipalWithOauth(MemberPrincipal principal) {
        return Member.builder()
                .email(principal.email())
                .password(UUID.randomUUID().toString())
                .nickname(principal.nickname())
                .loginType(LoginType.OAUTH)
                .userRole(UserRole.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }
}
