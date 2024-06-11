package freshtrash.freshtrashbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.dto.request.AuctionRequest;
import freshtrash.freshtrashbackend.dto.request.BiddingRequest;
import freshtrash.freshtrashbackend.dto.response.AuctionResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import freshtrash.freshtrashbackend.service.AuctionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(AuctionApi.class)
@Import(TestSecurityConfig.class)
class AuctionApiTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuctionService auctionService;

    @DisplayName("경매 추가 요청")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void given_imageAndAuctionRequestAndLoginUser_when_addAuction_then_returnSavedAuctionData() throws Exception {
        // given
        Long memberId = 123L;
        MockMultipartFile image = Fixture.createMultipartFileOfImage("image");
        AuctionRequest auctionRequest = FixtureDto.createAuctionRequest();
        Auction auction = Auction.fromRequest(auctionRequest, "test.png", memberId);
        MemberPrincipal memberPrincipal = FixtureDto.createMemberPrincipal();
        AuctionResponse auctionResponse = AuctionResponse.fromEntity(auction, memberPrincipal);
        given(auctionService.addAuction(
                        any(MultipartFile.class), any(AuctionRequest.class), any(MemberPrincipal.class)))
                .willReturn(auctionResponse);
        // when
        mvc.perform(multipart(HttpMethod.POST, "/api/v1/auctions")
                        .file("imgFile", image.getBytes())
                        .file(Fixture.createMultipartFileOfJson(
                                "auctionRequest", objectMapper.writeValueAsString(auctionRequest)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(auctionRequest.title()))
                .andExpect(jsonPath("$.content").value(auctionRequest.content()))
                .andExpect(jsonPath("$.finalBid").value(auctionRequest.minimumBid()));
        // then
    }

    @DisplayName("경매 목록 조회")
    @Test
    void given_queryParamAndPageable_when_getAuction_then_pagingAuctionData() throws Exception {
        // given
        AuctionResponse auctionResponse = AuctionResponse.fromEntity(Fixture.createAuction());
        given(auctionService.getAuctions(any(Predicate.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(auctionResponse)));
        // when
        mvc.perform(get("/api/v1/auctions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(1));
        // then
    }

    @DisplayName("경매 단일 조회")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void given_auctionIdAndLoginUser_when_getAuction_then_returnSingleAuctionData() throws Exception {
        // given
        Auction auction = Fixture.createAuction();
        given(auctionService.getAuction(eq(auction.getId()))).willReturn(auction);
        // when
        mvc.perform(get("/api/v1/auctions/" + auction.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(auction.getId()));
        // then
    }

    @DisplayName("경매 삭제 요청")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void given_auctionIdAndLoginUser_when_loginUserIsWriterOrAdmin_then_deleteAuction() throws Exception {
        // given
        Long auctionId = 1L, memberId = 123L;
        UserRole userRole = UserRole.USER;
        willDoNothing().given(auctionService).deleteAuction(auctionId, userRole, memberId);
        // when
        mvc.perform(delete("/api/v1/auctions/" + auctionId)).andExpect(status().isNoContent());
        // then
    }

    @DisplayName("경매 입찰 요청")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void bidding() throws Exception {
        // given
        Long auctionId = 2L, memberId = 123L;
        int biddingPrice = 10000;
        BiddingRequest biddingRequest = FixtureDto.createBiddingRequest(biddingPrice);
        willDoNothing().given(auctionService).requestBidding(auctionId, biddingPrice, memberId);
        // when
        mvc.perform(put("/api/v1/auctions/" + auctionId + "/bid")
                        .content(objectMapper.writeValueAsString(biddingRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        // then
    }
}
