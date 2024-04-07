package freshtrash.freshtrashbackend.security;

import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.constants.UserRole;

public record TokenInfo(
        Long id, String email, String nickname, UserRole userRole, double rating, String fileName, Address address) {
    public static TokenInfo of(
            String email, String nickname, String spec, double rating, String fileName, Address address) {
        String[] sepcs = spec.split(":");
        return new TokenInfo(
                Long.parseLong(sepcs[0]),
                email,
                nickname,
                UserRole.valueOf(sepcs[1].substring(5)),
                rating,
                fileName,
                address);
    }

    public static TokenInfo ofAnonymous() {
        return new TokenInfo(null, "ANONYMOUS", "ANONYMOUS", UserRole.ANONYMOUS, 0, null, null);
    }

    public MemberPrincipal toMemberPrincipal() {
        return MemberPrincipal.of(id, email, null, nickname, userRole, rating, fileName, address);
    }
}
