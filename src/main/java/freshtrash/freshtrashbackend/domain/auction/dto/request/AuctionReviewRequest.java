package freshtrash.freshtrashbackend.domain.auction.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public record AuctionReviewRequest(@NotNull @Max(5) Integer rate, String content) {}
