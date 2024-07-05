package freshtrash.freshtrashbackend.security;

import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import lombok.Builder;

@Builder
public record TokenInfo(
        Long id, String email, String nickname, UserRole userRole, double rating, String fileName, Address address) {}
