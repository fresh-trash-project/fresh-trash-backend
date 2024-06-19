package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.request.ReviewRequest;
import freshtrash.freshtrashbackend.dto.response.ProductReviewResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.service.ProductReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductReviewApi {
    private final ProductReviewService productReviewService;

    /**
     * 폐기물 리뷰 작성
     */
    @PostMapping("/{productId}/reviews")
    public ResponseEntity<ProductReviewResponse> addProductReview(
            @RequestBody @Valid ReviewRequest reviewRequest,
            @PathVariable Long productId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        // TODO : productDeal 거래 확인
        ProductReviewResponse productReviewResponse = ProductReviewResponse.fromEntity(
                productReviewService.insertProductReview(reviewRequest, productId, memberPrincipal.id()));
        return ResponseEntity.status(HttpStatus.CREATED).body(productReviewResponse);
    }
}
