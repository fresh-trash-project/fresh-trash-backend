package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.entity.constants.AccountStatus;
import freshtrash.freshtrashbackend.entity.constants.LoginType;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="members")
@Getter
@Setter
@ToString
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column
    private double rating;

    @Column
    private String nickname;

    @Column
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column
    private LoginType loginType;

    @Enumerated(EnumType.STRING)
    @Column
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column
    private AccountStatus accountStatus;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Embedded
    private Address address;

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
}

