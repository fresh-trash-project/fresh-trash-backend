package freshtrash.freshtrashbackend.dto.response;

import freshtrash.freshtrashbackend.dto.constants.SellType;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.Product;
import freshtrash.freshtrashbackend.entity.constants.ProductCategory;
import freshtrash.freshtrashbackend.entity.constants.ProductStatus;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProductResponse(
        Long id,
        String title,
        String content,
        SellType sellType,
        Integer productPrice,
        Integer likeCount,
        Integer viewCount,
        String fileName,
        ProductCategory productCategory,
        ProductStatus productStatus,
        SellStatus sellStatus,
        Address address,
        LocalDateTime createdAt,
        MemberResponse memberResponse) {

    public static class ProductResponseBuilder {
        public ProductResponseBuilder productPrice(Integer productPrice) {
            this.productPrice = productPrice;
            this.sellType = productPrice == 0 ? SellType.SHARE : SellType.TRANSACTION;
            return this;
        }
    }

    public static ProductResponse fromEntity(Product product) {
        return ProductResponse.of(product, MemberResponse.fromEntity(product.getMember()));
    }

    public static ProductResponse fromEntity(Product product, MemberPrincipal memberPrincipal) {
        return ProductResponse.of(product, MemberResponse.fromPrincipal(memberPrincipal));
    }

    private static ProductResponse of(Product product, MemberResponse memberResponse) {
        return ProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .content(product.getContent())
                .productPrice(product.getProductPrice())
                .likeCount(product.getLikeCount())
                .viewCount(product.getViewCount())
                .fileName(product.getFileName())
                .productCategory(product.getProductCategory())
                .productStatus(product.getProductStatus())
                .sellStatus(product.getSellStatus())
                .address(product.getAddress())
                .createdAt(product.getCreatedAt())
                .memberResponse(memberResponse)
                .build();
    }
}
