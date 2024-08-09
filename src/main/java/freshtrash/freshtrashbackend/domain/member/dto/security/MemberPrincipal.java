package freshtrash.freshtrashbackend.domain.member.dto.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import freshtrash.freshtrashbackend.domain.member.entity.Address;
import freshtrash.freshtrashbackend.domain.member.entity.Member;
import freshtrash.freshtrashbackend.domain.member.entity.constants.UserRole;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Builder
public record MemberPrincipal(
        Long id,
        String email,
        @JsonIgnore String password,
        @JsonIgnore Collection<? extends GrantedAuthority> authorities,
        UserRole userRole,
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

    @JsonIgnore
    public static MemberPrincipal fromEntity(Member member) {
        return MemberPrincipal.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .nickname(member.getNickname())
                .authorities(member.getUserRole())
                .userRole(member.getUserRole())
                .rating(member.getRating())
                .fileName(member.getFileName())
                .address(member.getAddress())
                .build();
    }

    @JsonIgnore
    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2Attributes;
    }

    @JsonIgnore
    @Override
    public String getName() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Objects.isNull(authorities) || authorities.isEmpty()
                ? Set.of(new SimpleGrantedAuthority(userRole.getName()))
                : authorities;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return email;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
