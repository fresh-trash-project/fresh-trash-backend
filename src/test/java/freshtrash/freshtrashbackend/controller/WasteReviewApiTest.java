package freshtrash.freshtrashbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.dto.request.ReviewRequest;
import freshtrash.freshtrashbackend.entity.WasteReview;
import freshtrash.freshtrashbackend.service.WasteReviewService;
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

@WebMvcTest(WasteReviewApi.class)
@Import(TestSecurityConfig.class)
class WasteReviewApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private WasteReviewService wasteReviewService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("폐기물 리뷰 작성")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    void given_reviewRequestAndWasteIdAndLoginUser_when_addWasteReview_then_returnReviewResponse() throws Exception {
        // given
        ReviewRequest reviewRequest = FixtureDto.createReviewRequest(4);
        Long wasteId = 1L;
        Long memberId = 123L;
        WasteReview wasteReview = WasteReview.fromRequest(reviewRequest, wasteId, memberId);
        given(wasteReviewService.insertWasteReview(reviewRequest, wasteId, memberId))
                .willReturn(wasteReview);
        // when
        mvc.perform(post("/api/v1/wastes/" + wasteId + "/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value(memberId))
                .andExpect(jsonPath("$.wasteId").value(wasteId))
                .andExpect(jsonPath("$.rating").value(reviewRequest.rate()));
        // then
    }
}