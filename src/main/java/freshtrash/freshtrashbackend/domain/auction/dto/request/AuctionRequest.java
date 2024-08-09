package freshtrash.freshtrashbackend.domain.auction.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import freshtrash.freshtrashbackend.domain.auction.entity.constants.AuctionStatus;
import freshtrash.freshtrashbackend.domain.product.entity.constants.ProductCategory;
import freshtrash.freshtrashbackend.domain.product.entity.constants.ProductStatus;
import freshtrash.freshtrashbackend.global.exception.AuctionException;
import freshtrash.freshtrashbackend.global.exception.constants.ErrorCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

public record AuctionRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotNull ProductCategory productCategory,
        @NotNull ProductStatus productStatus,
        @NotNull AuctionStatus auctionStatus,
        @PositiveOrZero int minimumBid,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startedAt,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endedAt) {

    public AuctionRequest {
        if (startedAt.isAfter(endedAt)) {
            throw new AuctionException(ErrorCode.INVALID_AUCTION_TIME);
        }
    }
}
