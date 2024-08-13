package freshtrash.freshtrashbackend.global.infra;

import freshtrash.freshtrashbackend.domain.product.entity.Product;
import freshtrash.freshtrashbackend.global.config.properties.RecSysProperties;
import freshtrash.freshtrashbackend.global.utils.RestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecSysService {
    private final RestUtils restUtils;
    private final RecSysProperties recSysProperties;

    /**
     * 상품 추가 시 해당 상품의 프로필 정보 추가
     */
    public void createProduct(Product product) {
        Map<String, Object> messageBody = new HashMap<>();
        messageBody.put("product_id", product.getId());
        messageBody.put("category", product.getProductCategory().getProfileIndex());
        messageBody.put("title", product.getTitle());
        messageBody.put("content", product.getContent());
        restUtils.post(new HttpEntity<>(messageBody), getUrl(recSysProperties.productEndpoint()), Void.class);
    }

    /**
     * 상품 수정 시 해당 상품의 프로필 정보 수정
     */
    public void updateProduct(Product product) {
        Map<String, Object> messageBody = new HashMap<>();
        messageBody.put("product_id", product.getId());
        messageBody.put("category", product.getProductCategory().getProfileIndex());
        messageBody.put("title", product.getTitle());
        messageBody.put("content", product.getContent());
        restUtils.put(new HttpEntity<>(messageBody), getUrl(recSysProperties.productEndpoint()), Void.class);
    }

    /**
     * 상품 구매 시 구매자의 프로필 정보 수정
     * 1. 구매 횟수 + 1
     * 2. 상품 프로필 누적 합 계산
     */
    public void purchaseProduct(Long productId, Long memberId) {
        restUtils.put(
                new HttpEntity<>(null), getUrl(recSysProperties.productPurchase(), productId, memberId), Void.class);
    }

    private String getUrl(String endpoint) {
        return String.format("%s%s", recSysProperties.host(), endpoint);
    }

    private String getUrl(String endpoint, Long id1, Long id2) {
        return String.format("%s%s%d/%d/", recSysProperties.host(), endpoint, id1, id2);
    }
}
