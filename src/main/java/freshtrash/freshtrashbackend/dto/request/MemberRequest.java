package freshtrash.freshtrashbackend.dto.request;

import freshtrash.freshtrashbackend.entity.Address;

import javax.validation.constraints.NotBlank;

public record MemberRequest(@NotBlank String nickname, Address address) {}
