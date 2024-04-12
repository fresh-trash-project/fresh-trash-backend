package freshtrash.freshtrashbackend.dto.response;

import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.entity.constants.AccountStatus;
import freshtrash.freshtrashbackend.entity.constants.LoginType;
import lombok.Builder;

@Builder
public record MemberResponse(
        Long id,
        String email,
        String nickname,
        double rating,
        String fileName,
        Address address,
        LoginType loginType,
        AccountStatus accountStatus) {
    public static MemberResponse fromEntity(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .rating(member.getRating())
                .fileName(member.getFileName())
                .address(member.getAddress())
                .loginType(member.getLoginType())
                .accountStatus(member.getAccountStatus())
                .build();
    }
}
