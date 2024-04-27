package freshtrash.freshtrashbackend.dto.response;

import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.Member;
import lombok.Builder;

@Builder
public record MemberResponse(Long id, String nickname, double rating, String fileName, Address address) {
    public static MemberResponse fromEntity(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .rating(member.getRating())
                .fileName(member.getFileName())
                .address(member.getAddress())
                .build();
    }

    public static MemberResponse fromPrincipal(MemberPrincipal memberPrincipal) {
        return MemberResponse.builder()
                .id(memberPrincipal.id())
                .nickname(memberPrincipal.nickname())
                .rating(memberPrincipal.rating())
                .fileName(memberPrincipal.fileName())
                .address(memberPrincipal.address())
                .build();
    }
}
