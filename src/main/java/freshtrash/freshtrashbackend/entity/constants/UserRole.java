package freshtrash.freshtrashbackend.entity.constants;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    ANONYMOUS("ROLE_ANONYMOUS");

    private final String name;

    UserRole(String name) {
        this.name = name;
    }
}
