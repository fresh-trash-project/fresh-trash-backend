package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.controller.constants.ProductDealMemberType;
import freshtrash.freshtrashbackend.dto.response.ProductResponse;
import freshtrash.freshtrashbackend.entity.ProductDealLog;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.repository.ChatRoomRepository;
import freshtrash.freshtrashbackend.repository.ProductDealLogRepository;
import freshtrash.freshtrashbackend.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductDealServiceTest {
    @InjectMocks
    private ProductDealService productDealService;

    @Mock
    private ProductDealLogRepository productDealLogRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Test
    @DisplayName("거래 내역 저장")
    void given_productAndChatRoomAndSellerAndBuyerAndSellStatus_when_then_updateSellStatusAndSaveLog() {
        // given
        Long productId = 1L;
        Long chatRoomId = 3L;
        Long sellerId = 1L;
        Long buyerId = 2L;
        SellStatus sellStatus = SellStatus.CLOSE;
        given(productDealLogRepository.save(any(ProductDealLog.class)))
                .willReturn(Fixture.createProductDealLog(productId, sellerId, buyerId));
        willDoNothing().given(productRepository).updateSellStatus(eq(productId), eq(sellStatus));
        willDoNothing().given(chatRoomRepository).updateSellStatus(eq(chatRoomId), eq(sellStatus));
        // when
        productDealService.completeProductDeal(productId, chatRoomId, sellerId, buyerId, sellStatus);
        ArgumentCaptor<ProductDealLog> captor = ArgumentCaptor.forClass(ProductDealLog.class);
        // then
        verify(productDealLogRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getProductId()).isEqualTo(productId);
        assertThat(captor.getValue().getSellerId()).isEqualTo(sellerId);
        assertThat(captor.getValue().getBuyerId()).isEqualTo(buyerId);
    }

    @ParameterizedTest
    @DisplayName("거래한 폐기물 목록 조회")
    @CsvSource(value = {"SELLER_CLOSE", "SELLER_ONGOING", "BUYER"})
    void given_memberIdAndMemberTypeAndPageable_when_getProductDealLogs_then_convertToProducts(
            ProductDealMemberType memberType) {
        // given
        Long memberId = 1L;
        int expectedSize = 1;
        Pageable pageable = PageRequest.of(0, 10);
        if (memberType == ProductDealMemberType.SELLER_CLOSE) {
            given(productDealLogRepository.findAllBySeller_Id(eq(memberId), eq(pageable)))
                    .willReturn(new PageImpl<>(List.of(Fixture.createProductDealLog())));
        } else if (memberType == ProductDealMemberType.BUYER) {
            given(productDealLogRepository.findAllByBuyer_Id(eq(memberId), eq(pageable)))
                    .willReturn(new PageImpl<>(List.of(Fixture.createProductDealLog())));
        } else {
            given(productRepository.findAllByMemberIdAndSellStatusNot(eq(memberId), eq(SellStatus.CLOSE), eq(pageable)))
                    .willReturn(new PageImpl<>(List.of(Fixture.createProduct())));
        }
        // when
        Page<ProductResponse> products = productDealService.getTransactedProducts(memberId, memberType, pageable);
        // then
        assertThat(products.getSize()).isEqualTo(expectedSize);
    }
}
