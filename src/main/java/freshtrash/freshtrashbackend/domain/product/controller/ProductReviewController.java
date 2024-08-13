package freshtrash.freshtrashbackend.domain.product.controller;

import freshtrash.freshtrashbackend.domain.member.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.domain.product.dto.request.ProductReviewRequest;
import freshtrash.freshtrashbackend.domain.product.dto.response.ProductReviewResponse;
import freshtrash.freshtrashbackend.domain.product.service.ProductReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductReviewController {
    private final ProductReviewService productReviewService;

    /**
     * 폐기물 리뷰 작성
     */
    @PostMapping("/{productId}/reviews")
    public ResponseEntity<ProductReviewResponse> addProductReview(
            @RequestBody @Valid ProductReviewRequest productReviewRequest,
            @PathVariable Long productId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        // TODO : productDeal 거래 확인
        ProductReviewResponse productReviewResponse = ProductReviewResponse.fromEntity(
                productReviewService.insertProductReview(productReviewRequest, productId, memberPrincipal.id()));
        return ResponseEntity.status(HttpStatus.CREATED).body(productReviewResponse);
    }
}
