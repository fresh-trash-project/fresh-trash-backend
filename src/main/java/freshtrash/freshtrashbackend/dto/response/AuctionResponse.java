package freshtrash.freshtrashbackend.dto.response;

import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.entity.constants.AuctionStatus;
import freshtrash.freshtrashbackend.entity.constants.ProductCategory;
import freshtrash.freshtrashbackend.entity.constants.ProductStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AuctionResponse(
        Long id,
        String title,
        String content,
        Integer viewCount,
        String fileName,
        ProductCategory productCategory,
        ProductStatus productStatus,
        AuctionStatus auctionStatus,
        int min_bid,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        LocalDateTime createdAt,
        MemberResponse memberResponse) {

    public static AuctionResponse fromEntity(Auction auction) {
        return AuctionResponse.of(auction, MemberResponse.fromEntity(auction.getMember()));
    }

    public static AuctionResponse fromEntity(Auction auction, MemberPrincipal memberPrincipal) {
        return AuctionResponse.of(auction, MemberResponse.fromPrincipal(memberPrincipal));
    }

    public static AuctionResponse of(Auction auction, MemberResponse memberResponse) {
        return AuctionResponse.builder()
                .id(auction.getId())
                .title(auction.getTitle())
                .content(auction.getContent())
                .viewCount(auction.getViewCount())
                .fileName(auction.getFileName())
                .productCategory(auction.getProductCategory())
                .productStatus(auction.getProductStatus())
                .auctionStatus(auction.getAuctionStatus())
                .min_bid(auction.getMin_bid())
                .startedAt(auction.getStartedAt())
                .endedAt(auction.getEndedAt())
                .createdAt(auction.getCreatedAt())
                .memberResponse(memberResponse)
                .build();
    }
}
