package freshtrash.freshtrashbackend.dto.security;

import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public record MemberPrincipal(
        Long id,
        String email,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        String nickname,
        double rating,
        String fileName,
        Address address)
        implements UserDetails {

    public static MemberPrincipal of(
            Long id,
            String email,
            String password,
            String nickname,
            UserRole userRole,
            double rating,
            String fileName,
            Address address) {
        return new MemberPrincipal(
                id,
                email,
                password,
                Set.of(new SimpleGrantedAuthority(userRole.getName())),
                nickname,
                rating,
                fileName,
                address);
    }

    /**
     * JWT 토큰 파싱 후 MemberPrincipal 생성 시 사용
     */
    public static MemberPrincipal of(
            Long id,
            String email,
            String nickname,
            UserRole userRole,
            double rating,
            String fileName,
            Address address) {
        return MemberPrincipal.of(id, email, null, nickname, userRole, rating, fileName, address);
    }

    public static MemberPrincipal fromEntity(Member member) {
        return MemberPrincipal.of(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                member.getNickname(),
                member.getUserRole(),
                member.getRating(),
                member.getFileName(),
                member.getAddress());
    }

    public UserRole getUserRole() {
        return authorities.stream()
                .map(r -> UserRole.valueOf(r.getAuthority().substring(5)))
                .findFirst()
                .orElse(UserRole.ANONYMOUS);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
