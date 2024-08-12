package freshtrash.freshtrashbackend.global.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RestUtils {
    private final RestTemplate restTemplate;

    public <T> ResponseEntity<T> post(
            HttpEntity<Map<String, Object>> httpEntity, String uri, Class<T> returnType) {
        return restTemplate.postForEntity(uri, httpEntity, returnType);
    }

    public <T> ResponseEntity<T> put(
            HttpEntity<Map<String, Object>> httpEntity, String uri, Class<T> returnType) {
        return restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, returnType);
    }
}
