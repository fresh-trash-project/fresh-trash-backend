package freshtrash.freshtrashbackend.controller;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.controller.constants.LikeStatus;
import freshtrash.freshtrashbackend.dto.response.ProductResponse;
import freshtrash.freshtrashbackend.entity.constants.ProductCategory;
import freshtrash.freshtrashbackend.service.ProductLikeService;
import freshtrash.freshtrashbackend.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.context.support.TestExecutionEvent.TEST_EXECUTION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(ProductLikeApi.class)
@Import(TestSecurityConfig.class)
class ProductLikeApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductLikeService productLikeService;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("관심 폐기물 목록 조회")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    void given_loginUserAndPageable_when_getLikedProducts_then_returnPagingProductData() throws Exception {
        // given
        ProductCategory category = ProductCategory.BEAUTY;
        Pageable pageable = PageRequest.of(0, 6, Sort.Direction.DESC, "createdAt");
        given(productLikeService.getLikedProducts(any(Predicate.class), eq(pageable)))
                .willReturn(new PageImpl<>(List.of(ProductResponse.fromEntity(Fixture.createProduct()))));
        // when
        mvc.perform(get("/api/v1/products/likes").queryParam("category", category.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(1));
        // then
    }

    @ParameterizedTest
    @DisplayName("관심 폐기물 추가/삭제")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    @CsvSource(value = {"LIKE", "UNLIKE"})
    void given_likeStatusAndProductIdAndLoginUser_when_addOrDeleteProductLike_then_returnBoolean(LikeStatus likeStatus) throws Exception {
        // given
        Long productId = 1L;
        Long memberId = 123L;
        willDoNothing().given(productLikeService).addProductLike(memberId, productId);
        willDoNothing().given(productLikeService).deleteProductLike(memberId, productId);
        // when
        mvc.perform(post("/api/v1/products/" + productId + "/likes").queryParam("likeStatus", String.valueOf(likeStatus)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(likeStatus==LikeStatus.LIKE));
        // then
    }
}