package freshtrash.freshtrashbackend.dto.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import freshtrash.freshtrashbackend.security.TokenInfo;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Builder
@RedisHash(value = "Member")
public record MemberPrincipal(
        @Id Long id,
        String email,
        @JsonIgnore String password,
        @JsonIgnore Collection<? extends GrantedAuthority> authorities,
        String nickname,
        double rating,
        String fileName,
        Address address,
        @JsonIgnore Map<String, Object> oAuth2Attributes)
        implements UserDetails, OAuth2User {

    public static class MemberPrincipalBuilder {
        public MemberPrincipalBuilder authorities(UserRole userRole) {
            this.authorities = Set.of(new SimpleGrantedAuthority(userRole.getName()));
            return this;
        }
    }

    public static MemberPrincipal fromEntity(Member member) {
        return MemberPrincipal.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .nickname(member.getNickname())
                .authorities(member.getUserRole())
                .rating(member.getRating())
                .fileName(member.getFileName())
                .address(member.getAddress())
                .build();
    }

    public static MemberPrincipal fromTokenInfo(TokenInfo tokenInfo) {
        return MemberPrincipal.builder()
                .id(tokenInfo.id())
                .email(tokenInfo.email())
                .nickname(tokenInfo.fileName())
                .authorities(tokenInfo.userRole())
                .rating(tokenInfo.rating())
                .fileName(tokenInfo.fileName())
                .address(tokenInfo.address())
                .build();
    }

    public UserRole getUserRole() {
        return authorities.stream()
                .map(r -> UserRole.valueOf(r.getAuthority().substring(5)))
                .findFirst()
                .orElse(UserRole.ANONYMOUS);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2Attributes;
    }

    @Override
    public String getName() {
        return email;
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
