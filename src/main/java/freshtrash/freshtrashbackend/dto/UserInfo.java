package freshtrash.freshtrashbackend.dto;

import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.Member;

public record UserInfo(String nickname, double rating, String fileName, Address address) {
    public static UserInfo fromEntity(Member member) {
        return new UserInfo(member.getNickname(), member.getRating(), member.getFileName(), member.getAddress());
    }
    public static UserInfo fromPrincipal(MemberPrincipal memberPrincipal) {
        return new UserInfo(memberPrincipal.nickname(), memberPrincipal.rating(), memberPrincipal.fileName(), memberPrincipal.address());
    }
}
