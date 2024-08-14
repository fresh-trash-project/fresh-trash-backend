package freshtrash.freshtrashbackend.global.infra;

import freshtrash.freshtrashbackend.domain.auction.entity.Auction;
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
     * 상품 추가/수정 시 해당 상품의 프로필 정보 수정
     */
    public void createOrUpdateProduct(Product product) {
        Map<String, Object> messageBody = new HashMap<>();
        messageBody.put("file_name", product.getProfileFileName());
        messageBody.put("category", product.getProductCategory().getProfileIndex());
        messageBody.put("title", product.getTitle());
        messageBody.put("content", product.getContent());
        restUtils.put(new HttpEntity<>(messageBody), getUrl(recSysProperties.profileEndpoint()), Void.class);
    }

    /**
     * 경매 추가/수정 시 해당 상품의 프로필 정보 수정
     */
    public void createAuction(Auction auction) {
        Map<String, Object> messageBody = new HashMap<>();
        messageBody.put("file_name", auction.getProfileFileName());
        messageBody.put("category", auction.getProductCategory().getProfileIndex());
        messageBody.put("title", auction.getTitle());
        messageBody.put("content", auction.getContent());
        restUtils.put(new HttpEntity<>(messageBody), getUrl(recSysProperties.profileEndpoint()), Void.class);
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

    /**
     * 낙찰된 경매 상품 구매 시 구매자의 프로필 정보 수정
     * 1. 구매 횟수 + 1
     * 2. 경매 프로필 누적 합 계산
     */
    public void purchaseAuction(Long auctionId, Long memberId) {
        restUtils.put(
                new HttpEntity<>(null), getUrl(recSysProperties.auctionPurchase(), auctionId, memberId), Void.class);
    }

    private String getUrl(String endpoint) {
        return String.format("%s%s", recSysProperties.host(), endpoint);
    }

    private String getUrl(String endpoint, Long id1, Long id2) {
        return String.format("%s%s%d/%d/", recSysProperties.host(), endpoint, id1, id2);
    }
}
