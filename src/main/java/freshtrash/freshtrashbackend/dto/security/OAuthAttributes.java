package freshtrash.freshtrashbackend.dto.security;

import freshtrash.freshtrashbackend.exception.AuthException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuthAttributes {
    private static final String NAVER_REGISTRATION_ID = "Naver";
    private static final String KAKAO_REGISTRATION_ID = "Kakao";
    private static final String GOOGLE_REGISTRATION_ID = "Google";
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String provider;

    public static OAuthAttributes of(
            String name, String email, String provider, Map<String, Object> attributes, String nameAttributeKey) {
        return new OAuthAttributes(attributes, nameAttributeKey, name, email, provider);
    }

    public static OAuthAttributes of(
            String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if (NAVER_REGISTRATION_ID.equalsIgnoreCase(registrationId)) {
            return ofNaver("id", attributes);
        } else if (KAKAO_REGISTRATION_ID.equalsIgnoreCase(registrationId)) {
            return ofKakao("id", attributes);
        }

        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.of(
                (String) attributes.get("name"),
                (String) attributes.get("email"),
                GOOGLE_REGISTRATION_ID,
                attributes,
                userNameAttributeName);
    }

    @SuppressWarnings("unchecked")
    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) Optional.of(attributes.get("response"))
                .orElseThrow(() -> new AuthException("Invalid Naver OAuth Request"));

        return OAuthAttributes.of(
                (String) response.get("name"),
                (String) response.get("email"),
                NAVER_REGISTRATION_ID,
                response,
                userNameAttributeName);
    }

    @SuppressWarnings("unchecked")
    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) Optional.of(attributes.get("kakao_account"))
                .orElseThrow(() -> new AuthException("Invalid Kakao OAuth Request"));
        Map<String, Object> account = (Map<String, Object>) Optional.of(response.get("profile"))
                .orElseThrow(() -> new AuthException("Invalid Kakao OAuth Request"));

        return OAuthAttributes.of(
                (String) account.get("nickname"),
                (String) response.get("email"),
                KAKAO_REGISTRATION_ID,
                attributes,
                userNameAttributeName);
    }
}
