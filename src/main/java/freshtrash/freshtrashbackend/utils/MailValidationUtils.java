package freshtrash.freshtrashbackend.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import freshtrash.freshtrashbackend.dto.properties.MailProperties;
import freshtrash.freshtrashbackend.exception.MailException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@RequiredArgsConstructor
public class MailValidationUtils {
    private final MailProperties mailProperties;
    private static final String CHECK_KEY_1 = "deliverability";
    private static final String CHECK_KEY_2 = "is_free_email";
    private static final String DELIVERABLE = "DELIVERABLE";

    /**
     * 메일 유효성 검증
     */
    public void isValidMail(String email) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(mailProperties.apiUrl() + "?api_key=" + mailProperties.apiKey() + "&email=" + email))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String result = response.body();
            JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
            String deliverable = jsonObject.get(CHECK_KEY_1).getAsString();

            boolean isFree =
                    jsonObject.get(CHECK_KEY_2).getAsJsonObject().get("value").getAsBoolean();

            if (!deliverable.equals(DELIVERABLE) || !isFree) {
                throw new MailException(ErrorCode.MAIL_NOT_VALID);
            }

        } catch (IOException | InterruptedException e) {
            throw new MailException(ErrorCode.MAIL_VALIDATION_FAIL, e);
        }
    }
}
