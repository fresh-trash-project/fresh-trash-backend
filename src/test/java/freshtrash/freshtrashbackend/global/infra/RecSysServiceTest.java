package freshtrash.freshtrashbackend.global.infra;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.domain.auction.entity.Auction;
import freshtrash.freshtrashbackend.domain.product.entity.Product;
import freshtrash.freshtrashbackend.global.config.properties.RecSysProperties;
import freshtrash.freshtrashbackend.global.utils.RestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class RecSysServiceTest {
    @InjectMocks
    private RecSysService recSysService;

    @Mock
    private RestUtils restUtils;

    @Mock
    private RecSysProperties recSysProperties;

    @DisplayName("상품의 프로필 정보를 수정하는 API를 요청한다.")
    @Test
    void given_product_when_requestPutByRest_then_returnVoid() {
        // given
        Product product = Fixture.createProduct();
        given(restUtils.put(any(HttpEntity.class), anyString(), eq(Void.class))).willReturn(ResponseEntity.ok(null));
        // when
        recSysService.createOrUpdateProduct(product);
        // then
    }

    @DisplayName("경매의 프로필 정보를 수정하는 API를 요청한다.")
    @Test
    void given_auction_when_requestPutByRest_then_returnVoid() {
        // given
        Auction auction = Fixture.createAuction();
        given(restUtils.put(any(HttpEntity.class), anyString(), eq(Void.class))).willReturn(ResponseEntity.ok(null));
        // when
        recSysService.createAuction(auction);
        // then
    }

    @DisplayName("상품 구매 시 회원의 구매 횟수와 프로필 정보를 업데이트하는 API를 요청한다.")
    @Test
    void given_productIdAndMemberId_when_requestPutByRest_then_returnVoid() {
        // given
        Long productId = 1L, memberId = 2L;
        given(restUtils.put(any(HttpEntity.class), anyString(), eq(Void.class))).willReturn(ResponseEntity.ok(null));
        // when
        recSysService.purchaseProduct(productId, memberId);
        // then
    }

    @DisplayName("낙찰된 경매 상품 구매 시 회원의 구매 횟수와 프로필 정보를 업데이트하는 API를 요청한다.")
    @Test
    void given_auctionIdAndMemberId_when_requestPutByRest_then_returnVoid() {
        // given
        Long auctionId = 1L, memberId = 2L;
        given(restUtils.put(any(HttpEntity.class), anyString(), eq(Void.class))).willReturn(ResponseEntity.ok(null));
        // when
        recSysService.purchaseAuction(auctionId, memberId);
        // then
    }
}