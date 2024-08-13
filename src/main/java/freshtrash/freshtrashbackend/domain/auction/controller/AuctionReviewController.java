package freshtrash.freshtrashbackend.domain.auction.controller;

import freshtrash.freshtrashbackend.domain.auction.dto.request.AuctionReviewRequest;
import freshtrash.freshtrashbackend.domain.auction.dto.response.AuctionReviewResponse;
import freshtrash.freshtrashbackend.domain.auction.service.AuctionReviewService;
import freshtrash.freshtrashbackend.domain.member.dto.security.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auctions")
public class AuctionReviewController {
    private final AuctionReviewService auctionReviewService;

    /**
     * 경매 낙찰 상품 리뷰 작성
     */
    @PostMapping("/{auctionId}/reviews")
    public ResponseEntity<AuctionReviewResponse> addAuctionReview(
            @RequestBody @Valid AuctionReviewRequest auctionReviewRequest,
            @PathVariable Long auctionId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        AuctionReviewResponse auctionReviewResponse = AuctionReviewResponse.fromEntity(
                auctionReviewService.insertAuctionReview(auctionReviewRequest, auctionId, memberPrincipal.id()));
        return ResponseEntity.status(HttpStatus.CREATED).body(auctionReviewResponse);
    }
}
