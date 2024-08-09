package freshtrash.freshtrashbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.domain.auction.controller.AuctionReviewController;
import freshtrash.freshtrashbackend.domain.auction.dto.request.AuctionReviewRequest;
import freshtrash.freshtrashbackend.domain.auction.entity.AuctionReview;
import freshtrash.freshtrashbackend.domain.auction.service.AuctionReviewService;
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
@WebMvcTest(AuctionReviewController.class)
@Import(TestSecurityConfig.class)
class AuctionReviewControllerTest {
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
        AuctionReviewRequest auctionReviewRequest = FixtureDto.createAuctionReviewRequest(3, "content");
        AuctionReview auctionReview = AuctionReview.fromRequest(auctionReviewRequest, auctionId, memberId);
        given(auctionReviewService.insertAuctionReview(auctionReviewRequest, auctionId, memberId))
                .willReturn(auctionReview);
        // when
        mvc.perform(post("/api/v1/auctions/" + auctionId + "/reviews")
                        .content(objectMapper.writeValueAsString(auctionReviewRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(auctionReviewRequest.rate()))
                .andExpect(jsonPath("$.content").value(auctionReviewRequest.content()));
        // then
    }
}