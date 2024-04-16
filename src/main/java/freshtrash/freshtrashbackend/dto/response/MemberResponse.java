package freshtrash.freshtrashbackend.dto.response;

import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.Member;
import lombok.Builder;

@Builder
public record MemberResponse(String nickname, double rating, String fileName, Address address) {
    public static MemberResponse fromEntity(Member member) {
        return MemberResponse.builder()
                .nickname(member.getNickname())
                .rating(member.getRating())
                .fileName(member.getFileName())
                .address(member.getAddress())
                .build();
    }
}