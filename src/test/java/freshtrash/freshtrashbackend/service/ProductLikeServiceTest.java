package freshtrash.freshtrashbackend.service;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.dto.response.ProductResponse;
import freshtrash.freshtrashbackend.entity.ProductLike;
import freshtrash.freshtrashbackend.entity.QProductLike;
import freshtrash.freshtrashbackend.entity.constants.ProductCategory;
import freshtrash.freshtrashbackend.repository.ProductLikeRepository;
import freshtrash.freshtrashbackend.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ProductLikeServiceTest {

    @InjectMocks
    private ProductLikeService productLikeService;

    @Mock
    private ProductLikeRepository productLikeRepository;

    @Mock
    private ProductRepository productRepository;

    @Test
    @DisplayName("관심 Product 목록 조회")
    void given_memberIdAndPageable_when_getProductLikes_then_convertToProductResponse() {
        // given
        Long memberId = 1L;
        ProductCategory category = ProductCategory.BEAUTY;
        int expectedSize = 1;
        Predicate predicate = QProductLike.productLike
                .memberId
                .eq(memberId)
                .and(QProductLike.productLike.product.productCategory.eq(category));
        Pageable pageable = PageRequest.of(0, 6, Sort.Direction.DESC, "createdAt");
        given(productLikeRepository.findAll(eq(predicate), eq(pageable)))
                .willReturn(new PageImpl<>(List.of(Fixture.createProductLike())));
        // when
        Page<ProductResponse> products = productLikeService.getLikedProducts(predicate, pageable);
        // then
        assertThat(products.getSize()).isEqualTo(expectedSize);
    }

    @Test
    @DisplayName("관심 폐기물 추가")
    void given_memberIdAndProductId_when_addProductLike_then_saveProductLikeAndUpdateLikeCount() {
        // given
        Long memberId = 123L, productId = 1L;
        ProductLike productLike = ProductLike.of(memberId, productId);
        given(productRepository.existsByIdAndMember_Id(eq(productId), eq(memberId)))
                .willReturn(false);
        given(productLikeRepository.existsByMemberIdAndProductId(memberId, productId))
                .willReturn(false);
        given(productLikeRepository.save(any(ProductLike.class))).willReturn(productLike);
        willDoNothing().given(productRepository).updateLikeCount(productId, 1);
        // when
        productLikeService.addProductLike(memberId, productId);
        // then
    }

    @Test
    @DisplayName("관심 폐기물 삭제")
    void given_memberIdAndProductId_when_deleteProductLike_then_deleteProductLikeAndUpdateLikeCount() {
        // given
        Long memberId = 123L, productId = 1L;
        given(productRepository.existsByIdAndMember_Id(eq(productId), eq(memberId)))
                .willReturn(false);
        given(productLikeRepository.existsByMemberIdAndProductId(memberId, productId))
                .willReturn(true);
        willDoNothing().given(productLikeRepository).deleteByMemberIdAndProductId(memberId, productId);
        willDoNothing().given(productRepository).updateLikeCount(productId, -1);
        // when
        productLikeService.deleteProductLike(memberId, productId);
        // then
    }
}