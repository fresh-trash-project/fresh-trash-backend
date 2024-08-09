package freshtrash.freshtrashbackend.domain.member.dto.request;

import freshtrash.freshtrashbackend.domain.member.entity.Address;

import javax.validation.constraints.NotBlank;

public record MemberRequest(@NotBlank String nickname, Address address) {}
