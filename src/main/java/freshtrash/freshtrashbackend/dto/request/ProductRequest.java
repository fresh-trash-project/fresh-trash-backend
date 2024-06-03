package freshtrash.freshtrashbackend.dto.request;

import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.constants.ProductStatus;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.entity.constants.ProductCategory;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record ProductRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotNull ProductCategory productCategory,
        @NotNull ProductStatus productStatus,
        @NotNull SellStatus sellStatus,
        @NotNull Integer productPrice,
        @NotNull Address address) {}
