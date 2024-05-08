package freshtrash.freshtrashbackend.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PasswordGenerator {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBER = "0123456789";
    private static final String SPECIAL_CHARACTER = "!@#$%^&*()-_=+";
    private static final int PASSWORD_LENGTH = 8;

    public static String generateTemporaryPassword() {
        StringBuilder password = new StringBuilder();
        List<Character> passwordChars = new ArrayList<>();
        SecureRandom random = new SecureRandom();

        // 영어 소문자, 숫자, 특수문자를 각각 한 개씩 포함
        passwordChars.add(getRandomCharacter(ALPHABET, random));
        passwordChars.add(getRandomCharacter(NUMBER, random));
        passwordChars.add(getRandomCharacter(SPECIAL_CHARACTER, random));

        for (int i = 3; i < PASSWORD_LENGTH; i++) {
            passwordChars.add(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        Collections.shuffle(passwordChars, new SecureRandom());
        passwordChars.forEach(password::append);

        return password.toString();
    }

    private static char getRandomCharacter(String characters, SecureRandom random) {
        return characters.charAt(random.nextInt(characters.length()));
    }
}