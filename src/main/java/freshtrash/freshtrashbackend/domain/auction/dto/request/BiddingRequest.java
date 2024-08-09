package freshtrash.freshtrashbackend.domain.auction.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public record BiddingRequest(@NotNull @PositiveOrZero Integer biddingPrice) {}
