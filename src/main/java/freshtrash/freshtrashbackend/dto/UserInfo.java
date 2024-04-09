package freshtrash.freshtrashbackend.dto;

import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.Member;
import lombok.Builder;

@Builder
public record UserInfo(String nickname, double rating, String fileName, Address address) {
    public static UserInfo fromEntity(Member member) {
        return UserInfo.builder()
                .nickname(member.getNickname())
                .rating(member.getRating())
                .fileName(member.getFileName())
                .address(member.getAddress())
                .build();
    }
    public static UserInfo fromPrincipal(MemberPrincipal memberPrincipal) {
        return UserInfo.builder()
                .nickname(memberPrincipal.nickname())
                .rating(memberPrincipal.rating())
                .fileName(memberPrincipal.fileName())
                .address(memberPrincipal.address())
                .build();
    }
}
