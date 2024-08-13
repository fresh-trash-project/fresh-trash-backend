package freshtrash.freshtrashbackend.domain.product.dto.request;

import freshtrash.freshtrashbackend.domain.member.entity.Address;
import freshtrash.freshtrashbackend.domain.product.entity.constants.ProductCategory;
import freshtrash.freshtrashbackend.domain.product.entity.constants.ProductSellStatus;
import freshtrash.freshtrashbackend.domain.product.entity.constants.ProductStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record ProductRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotNull ProductCategory productCategory,
        @NotNull ProductStatus productStatus,
        @NotNull ProductSellStatus productSellStatus,
        @NotNull Integer productPrice,
        @NotNull Address address) {}
