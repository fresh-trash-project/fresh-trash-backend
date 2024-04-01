package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.entity.constants.AccountStatus;
import freshtrash.freshtrashbackend.entity.constants.LoginType;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table(name="members")
@Builder
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
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
}

