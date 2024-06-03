package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.controller.constants.ProductDealMemberType;
import freshtrash.freshtrashbackend.dto.response.ProductResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.service.ProductDealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/productDeals")
public class ProductDealApi {
    private final ProductDealService productDealService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getDealtProducts(
            @RequestParam ProductDealMemberType memberType,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @PageableDefault(size = 6, sort = "createdAt", direction = DESC) Pageable pageable) {

        Page<ProductResponse> products = productDealService.getTransactedProducts(memberPrincipal.id(), memberType, pageable);
        return ResponseEntity.ok(products);
    }
}
