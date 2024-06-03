package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.controller.constants.ProductDealMemberType;
import freshtrash.freshtrashbackend.dto.response.ProductResponse;
import freshtrash.freshtrashbackend.service.ProductDealService;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.context.support.TestExecutionEvent.TEST_EXECUTION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@WebMvcTest(ProductDealApi.class)
class ProductDealApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductDealService productDealService;

    @ParameterizedTest
    @DisplayName("판매/구매 폐기물 목록 조회")
    @CsvSource(value = {"SELLER_ONGOING", "SELLER_CLOSE", "BUYER"})
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    void given_memberTypeAndLoginUserAndPageable_when_then_getPagingProductData(ProductDealMemberType memberType)
            throws Exception {
        // given
        Long memberId = 123L;
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "createdAt"));
        given(productDealService.getTransactedProducts(eq(memberId), eq(memberType), eq(pageable)))
                .willReturn(new PageImpl<>(List.of(ProductResponse.fromEntity(Fixture.createProduct()))));
        // when
        mvc.perform(get("/api/v1/productDeals").queryParam("memberType", memberType.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(1));

        // then
    }
}
