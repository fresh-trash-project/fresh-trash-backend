package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.request.ReviewRequest;
import freshtrash.freshtrashbackend.dto.response.ReviewResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.service.AuctionReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auctions")
public class AuctionReviewApi {
    private final AuctionReviewService auctionReviewService;

    /**
     * 경매 낙찰 상품 리뷰 작성
     */
    @PostMapping("/{auctionId}/reviews")
    public ResponseEntity<ReviewResponse> addAuctionReview(
            @RequestBody @Valid ReviewRequest reviewRequest,
            @PathVariable Long auctionId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        ReviewResponse reviewResponse = ReviewResponse.fromEntity(
                auctionReviewService.insertAuctionReview(reviewRequest, auctionId, memberPrincipal.id()));
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewResponse);
    }
}
