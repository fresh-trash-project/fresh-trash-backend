package freshtrash.freshtrashbackend.controller;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.controller.constants.LikeStatus;
import freshtrash.freshtrashbackend.dto.response.ApiResponse;
import freshtrash.freshtrashbackend.dto.response.ProductResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.ProductLike;
import freshtrash.freshtrashbackend.entity.QProductLike;
import freshtrash.freshtrashbackend.exception.ProductException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.service.ProductLikeService;
import freshtrash.freshtrashbackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductLikeApi {
    private final ProductService productService;
    private final ProductLikeService productLikeService;

    @GetMapping("/likes")
    public ResponseEntity<Page<ProductResponse>> getLikedProducts(
            @QuerydslPredicate(root = ProductLike.class) Predicate predicate,
            @PageableDefault(size = 6, sort = "createdAt", direction = DESC) Pageable pageable,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        predicate = QProductLike.productLike.memberId.eq(memberPrincipal.id()).and(predicate);
        Page<ProductResponse> products = productLikeService.getLikedProducts(predicate, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * 폐기물 관심 추가 또는 삭제
     */
    @PostMapping("/{productId}/likes")
    public ResponseEntity<ApiResponse<Boolean>> addOrDeleteProductLike(
            @RequestParam LikeStatus likeStatus,
            @PathVariable Long productId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        checkIfNotWriter(memberPrincipal, productId);
        Boolean isLike = likeStatus == LikeStatus.LIKE;
        if (isLike) {
            productLikeService.addProductLike(memberPrincipal.id(), productId);
        } else {
            productLikeService.deleteProductLike(memberPrincipal.id(), productId);
        }

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.of(isLike));
    }

    /**
     * 작성자인지 확인 (작성자인 경우 관심 추가/삭제 할수 없음)
     */
    private void checkIfNotWriter(MemberPrincipal memberPrincipal, Long productId) {
        if (productService.isWriterOfArticle(productId, memberPrincipal.id())) {
            throw new ProductException(ErrorCode.OWNER_PRODUCT_CANT_LIKE);
        }
    }
}
