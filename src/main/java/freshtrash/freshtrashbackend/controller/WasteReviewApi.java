package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.request.ReviewRequest;
import freshtrash.freshtrashbackend.dto.response.ReviewResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.service.WasteReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wastes")
public class WasteReviewApi {
    private final WasteReviewService wasteReviewService;

    /**
     * 폐기물 리뷰 작성
     */
    @PostMapping("/{wasteId}/reviews")
    private ResponseEntity<ReviewResponse> addWasteReview(
            @RequestBody @Valid ReviewRequest reviewRequest,
            @PathVariable Long wasteId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        // TODO : transaction 거래 확인
        ReviewResponse reviewResponse = ReviewResponse.fromEntity(
                wasteReviewService.insertWasteReview(reviewRequest, wasteId, memberPrincipal.id()));
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewResponse);
    }
}
