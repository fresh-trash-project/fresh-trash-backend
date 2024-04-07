package freshtrash.freshtrashbackend.security;

import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import lombok.Builder;

@Builder
public record TokenInfo(
        Long id, String email, String nickname, UserRole userRole, double rating, String fileName, Address address) {

    public static TokenInfo ofAnonymous() {
        return TokenInfo.builder()
                .email("ANONYMOUS")
                .nickname("ANONYMOUS")
                .userRole(UserRole.ANONYMOUS)
                .rating(0)
                .build();
    }

    public MemberPrincipal toMemberPrincipal() {
        return MemberPrincipal.fromTokenInfo(this);
    }
}
