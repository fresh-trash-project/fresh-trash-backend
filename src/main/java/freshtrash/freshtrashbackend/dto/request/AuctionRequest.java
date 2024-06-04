package freshtrash.freshtrashbackend.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import freshtrash.freshtrashbackend.entity.constants.AuctionStatus;
import freshtrash.freshtrashbackend.entity.constants.ProductCategory;
import freshtrash.freshtrashbackend.entity.constants.ProductStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AuctionRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotNull ProductCategory productCategory,
        @NotNull ProductStatus productStatus,
        @NotNull AuctionStatus auctionStatus,
        @NotNull Integer minBid,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startedAt,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endedAt) {}
