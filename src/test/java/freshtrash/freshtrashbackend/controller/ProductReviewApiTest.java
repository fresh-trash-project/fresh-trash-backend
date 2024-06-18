package freshtrash.freshtrashbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.dto.request.ReviewRequest;
import freshtrash.freshtrashbackend.entity.ProductReview;
import freshtrash.freshtrashbackend.service.ProductReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.context.support.TestExecutionEvent.TEST_EXECUTION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductReviewApi.class)
@Import(TestSecurityConfig.class)
class ProductReviewApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductReviewService productReviewService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("폐기물 리뷰 작성")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    void given_reviewRequestAndProductIdAndLoginUser_when_addProductReview_then_returnReviewResponse()
            throws Exception {
        // given
        ReviewRequest reviewRequest = FixtureDto.createReviewRequest(4);
        Long productId = 1L;
        Long memberId = 123L;
        ProductReview productReview = ProductReview.fromRequest(reviewRequest, productId, memberId);
        given(productReviewService.insertProductReview(reviewRequest, productId, memberId))
                .willReturn(productReview);
        // when
        mvc.perform(post("/api/v1/products/" + productId + "/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value(memberId))
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.rating").value(reviewRequest.rate()));
        // then
    }
}