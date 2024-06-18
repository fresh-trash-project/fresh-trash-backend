package freshtrash.freshtrashbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.dto.request.ReviewRequest;
import freshtrash.freshtrashbackend.entity.AuctionReview;
import freshtrash.freshtrashbackend.service.AuctionReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(AuctionReviewApi.class)
@Import(TestSecurityConfig.class)
class AuctionReviewApiTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuctionReviewService auctionReviewService;

    @Test
    @DisplayName("구매자는 결제한 낙찰 상품을 수령받고 리뷰를 작성한다.")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void given_reviewRequestAndAuctionIdAndLoginUser_when_then_insertReview() throws Exception {
        // given
        Long auctionId = 2L, memberId = 123L;
        ReviewRequest reviewRequest = FixtureDto.createReviewRequest(3, "content");
        AuctionReview auctionReview = AuctionReview.fromRequest(reviewRequest, auctionId, memberId);
        given(auctionReviewService.insertAuctionReview(reviewRequest, auctionId, memberId)).willReturn(auctionReview);
        // when
        mvc.perform(post("/api/v1/auctions/" + auctionId + "/reviews")
                        .content(objectMapper.writeValueAsString(reviewRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(reviewRequest.rate()))
                .andExpect(jsonPath("$.content").value(reviewRequest.content()));
        // then
    }
}