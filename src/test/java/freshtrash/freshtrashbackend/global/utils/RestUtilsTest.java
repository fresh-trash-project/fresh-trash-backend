package freshtrash.freshtrashbackend.global.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class RestUtilsTest {
    @InjectMocks private RestUtils restUtils;
    @Mock private RestTemplate restTemplate;

    @DisplayName("RestTemplate으로 POST 요청")
    @Test
    void given_httpEntityAndURIAndReturnType_when_requestPost_then_returnObjectOfReturnType() {
        //given
        Map<String, Object> messageBody = new HashMap<>();
        messageBody.put("product_id", 1L);
        messageBody.put("category", 2);
        messageBody.put("title", "title123");
        messageBody.put("content", "content123");
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(messageBody);
        String uri = "http://localhost/test-api";
        given(restTemplate.postForEntity(anyString(), any(HttpEntity.class), any(Class.class))).willReturn(null);
        //when
        restUtils.post(httpEntity, uri, Void.class);
        //then
    }

    @DisplayName("RestTemplate으로 PUT 요청")
    @Test
    void given_httpEntityAndURIAndReturnType_when_requestPut_then_returnObjectOfReturnType() {
        //given
        Map<String, Object> messageBody = new HashMap<>();
        messageBody.put("product_id", 1L);
        messageBody.put("category", 2);
        messageBody.put("title", "title123");
        messageBody.put("content", "content123");
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(messageBody);
        String uri = "http://localhost/test-api";
        given(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), any(Class.class))).willReturn(null);
        //when
        restUtils.put(httpEntity, uri, Void.class);
        //then
    }
}