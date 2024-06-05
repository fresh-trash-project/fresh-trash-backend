package freshtrash.freshtrashbackend.service;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.dto.request.AuctionRequest;
import freshtrash.freshtrashbackend.dto.response.AuctionResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.entity.QAuction;
import freshtrash.freshtrashbackend.repository.AuctionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {
    @InjectMocks
    private AuctionService auctionService;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private LocalFileService fileService;

    @DisplayName("경매 추가")
    @Test
    void given_imageAndAuctionRequestData_when_addAuction_then_returnSavedAuctionData() {
        // given
        AuctionRequest auctionRequest = FixtureDto.createAuctionRequest();
        MemberPrincipal memberPrincipal = FixtureDto.createMemberPrincipal();
        MockMultipartFile image = Fixture.createMultipartFileOfImage("image");
        String fileName = "test.png";
        Auction auction = Auction.fromRequest(auctionRequest, fileName, memberPrincipal.id());
        given(auctionRepository.save(any(Auction.class))).willReturn(auction);
        willDoNothing().given(fileService).uploadFile(eq(image), anyString());
        // when
        AuctionResponse auctionResponse = auctionService.addAuction(image, auctionRequest, memberPrincipal);
        // then
        assertThat(auctionResponse.title()).isEqualTo(auctionRequest.title());
        assertThat(auctionResponse.content()).isEqualTo(auctionRequest.content());
        assertThat(auctionResponse.productCategory()).isEqualTo(auctionRequest.productCategory());
        assertThat(auctionResponse.productStatus()).isEqualTo(auctionRequest.productStatus());
        assertThat(auctionResponse.auctionStatus()).isEqualTo(auctionRequest.auctionStatus());
        assertThat(auctionResponse.minBid()).isEqualTo(auctionRequest.minBid());
        assertThat(auctionResponse.startedAt()).isEqualTo(auctionRequest.startedAt());
        assertThat(auctionResponse.endedAt()).isEqualTo(auctionRequest.endedAt());
    }

    @DisplayName("경매 목록 조회")
    @Test
    void given_queryParamAndPageable_when_getAuction_then_pagingAuctionData() {
        // given
        int expectedSize = 1;
        Predicate predicate = QAuction.auction.title.equalsIgnoreCase("title");
        Pageable pageable = PageRequest.of(0, 6);
        given(auctionRepository.findAll(eq(predicate), eq(pageable)))
                .willReturn(new PageImpl<>(List.of(Fixture.createAuction())));
        // when
        Page<AuctionResponse> auctions = auctionService.getAuctions(predicate, pageable);
        // then
        assertThat(auctions.getTotalElements()).isEqualTo(expectedSize);
    }

    @DisplayName("경매 단일 조회")
    @Test
    void given_auctionId_when_getAuction_then_returnSingleAuction() {
        // given
        Auction expectedAuction = Fixture.createAuction();
        given(auctionRepository.findById(eq(expectedAuction.getId()))).willReturn(Optional.of(expectedAuction));
        // when
        Auction auction = auctionService.getAuction(expectedAuction.getId());
        // then
        assertThat(auction.getId()).isEqualTo(expectedAuction.getId());
    }

    @DisplayName("경매 삭제")
    @Test
    void given_auctionId_when_then_deleteAuction() {
        //given
        Long auctionId = 1L;
        willDoNothing().given(auctionRepository).deleteById(auctionId);
        //when
        auctionService.deleteAuction(auctionId);
        //then
    }
}