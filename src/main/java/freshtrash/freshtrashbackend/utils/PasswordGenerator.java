package freshtrash.freshtrashbackend.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGenerator {

    // 영어 소문자, 숫자, 특수문자를 포함한 모든 문자들
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";

    private static final int PASSWORD_LENGTH = 8;

    public static String generateTemporaryPassword() {
        StringBuilder password = new StringBuilder();
        List<Character> passwordChars = new ArrayList<>();
        SecureRandom random = new SecureRandom();

        // 영어 소문자, 숫자, 특수문자를 각각 한 개씩 포함
        passwordChars.add(getRandomCharacter("abcdefghijklmnopqrstuvwxyz", random));
        passwordChars.add(getRandomCharacter("0123456789", random));
        passwordChars.add(getRandomCharacter("!@#$%^&*()-_=+", random));

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