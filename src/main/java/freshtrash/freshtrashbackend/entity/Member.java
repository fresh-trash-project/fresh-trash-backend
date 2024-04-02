package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.entity.constants.AccountStatus;
import freshtrash.freshtrashbackend.entity.constants.LoginType;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="members")
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = "id")
public class Member implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private double rating;

    @Column(nullable = false)
    private String nickname;

    @Embedded
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

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    public Member(Long id, String email, double rating, String nickname, String fileName, LoginType loginType, UserRole userRole, AccountStatus accountStatus, LocalDateTime createdAt, LocalDateTime modifiedAt, Address address) {
        this.id = id;
        this.email = email;
        this.rating = rating;
        this.nickname = nickname;
        this.fileName = fileName;
        this.loginType = loginType;
        this.userRole = userRole;
        this.accountStatus = accountStatus;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
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

