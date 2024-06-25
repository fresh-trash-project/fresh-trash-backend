package freshtrash.freshtrashbackend.service;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.dto.request.AuctionRequest;
import freshtrash.freshtrashbackend.dto.response.AuctionResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.entity.QAuction;
import freshtrash.freshtrashbackend.entity.constants.AuctionStatus;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
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

    @Mock
    private BiddingHistoryService biddingHistoryService;

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
        assertThat(auctionResponse.finalBid()).isEqualTo(auctionRequest.minimumBid());
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

    @DisplayName("입찰 내역과 함께 경매 상세 조회")
    @Test
    void given_auctionId_when_getAuction_then_returnSingleAuctionWithBiddingHistory() {
        // given
        Long auctionId = 1L;
        given(auctionRepository.findWithBiddingHistoryById(auctionId)).willReturn(Optional.of(Fixture.createAuction()));
        // when
        Auction auctionWithBiddingHistory = auctionService.getAuctionWithBiddingHistory(auctionId);
        // then
        assertThat(auctionWithBiddingHistory).isNotNull();
    }

    @DisplayName("경매 삭제")
    @Test
    void given_auctionId_when_then_deleteAuction() {
        // given
        Long auctionId = 1L;
        willDoNothing().given(auctionRepository).deleteById(auctionId);
        // when
        assertThatCode(() -> auctionService.deleteAuction(auctionId)).doesNotThrowAnyException();
        // then
    }

    @DisplayName("경매 입찰")
    @Test
    void given_auctionIdAndBiddingPriceAndMemberId_when_passedDefenseLogic_then_updatePriceAndAddHistory() {
        // given
        Long auctionId = 1L, memberId = 3L;
        int biddingPrice = 10000;
        Auction auction = Fixture.createAuction();
        given(auctionRepository.findById(eq(auctionId))).willReturn(Optional.of(auction));
        willDoNothing().given(biddingHistoryService).addBiddingHistory(auctionId, memberId, biddingPrice);
        // when
        auctionService.requestBidding(auctionId, biddingPrice, memberId);
        // then
    }

    @DisplayName("경매 판매 상태를 CLOSE로 변경")
    @Test
    void given_auctionId_when_then_updateAuctionStatusToCLOSE() {
        // given
        Long auctionId = 1L;
        willDoNothing().given(auctionRepository).closeAuctionById(auctionId);
        // when
        assertThatCode(() -> auctionService.closeAuction(auctionId)).doesNotThrowAnyException();
        // then
    }

    @DisplayName("경매 판매 상태를 CANCEL로 변경")
    @Test
    void given_auctionId_when_then_updateAuctionStatusToCANCEL() {
        // given
        Long auctionId = 1L;
        willDoNothing().given(auctionRepository).cancelAuctionById(auctionId);
        // when
        assertThatCode(() -> auctionService.cancelAuction(auctionId)).doesNotThrowAnyException();
        // then
    }

    @DisplayName("마감되었지만 ONGOING 상태인 경매를 모두 조회")
    @Test
    void should_findAuctions_when_endedAndAuctionStatusIsONGOING() {
        // given
        Auction auction = Fixture.createAuction();
        ReflectionTestUtils.setField(auction, "auctionStatus", AuctionStatus.ONGOING);
        ReflectionTestUtils.setField(auction, "endedAt", LocalDateTime.now().minusDays(1));
        given(auctionRepository.findAllEndedAuctions()).willReturn(List.of(auction));
        // when
        List<Auction> endedAuctions = auctionService.getEndedAuctions();
        // then
        assertThat(endedAuctions.size()).isEqualTo(1);
        assertThat(endedAuctions.get(0).getAuctionStatus()).isEqualTo(AuctionStatus.ONGOING);
        assertThat(endedAuctions.get(0).getEndedAt()).isBefore(LocalDateTime.now());
    }

    @DisplayName("auctionId와 memberId를 입력받아 memberId가 판매자인지 확인하여 boolean 값으로 반환한다.")
    @Test
    void given_auctionIdAndMemberId_when_isSeller_then_returnTrue() {
        // given
        Long auctionId = 1L, memberId = 2L;
        given(auctionRepository.existsByIdAndMemberId(auctionId, memberId)).willReturn(true);
        // when
        boolean isSeller = auctionService.isSeller(auctionId, memberId);
        // then
        assertThat(isSeller).isTrue();
    }

    @DisplayName("auctionId, userRole, memberId를 입력받아 작성자일 경우 아무것도 반환하지 않는다.")
    @Test
    void given_auctionIdAndUserRoleAndMemberId_when_adminOrWriter_then_notAnyReturn() {
        // given
        Long auctionId = 1L, memberId = 2L;
        UserRole userRole = UserRole.USER;
        given(auctionRepository.existsByIdAndMemberId(auctionId, memberId)).willReturn(true);
        // when
        assertThatCode(() -> auctionService.checkIfWriterOrAdmin(auctionId, userRole, memberId))
                .doesNotThrowAnyException();
        // then
    }
}