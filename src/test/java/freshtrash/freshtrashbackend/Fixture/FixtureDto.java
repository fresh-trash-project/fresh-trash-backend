package freshtrash.freshtrashbackend.Fixture;

import freshtrash.freshtrashbackend.dto.request.*;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.constants.*;

import java.time.LocalDateTime;

public class FixtureDto {

    public static ProductRequest createProductRequest(
            String title,
            String content,
            ProductCategory productCategory,
            ProductStatus productStatus,
            SellStatus sellStatus,
            Integer productPrice,
            Address address) {
        return new ProductRequest(
                title,
                content,
                productCategory,
                productStatus,
                sellStatus,
                productPrice,
                address.allBlank() ? null : address);
    }

    public static ProductRequest createProductRequest() {
        return new ProductRequest(
                "title",
                "content",
                ProductCategory.BEAUTY,
                ProductStatus.BEST,
                SellStatus.CLOSE,
                0,
                Fixture.createAddress());
    }

    public static MemberPrincipal createMemberPrincipal() {
        return MemberPrincipal.builder()
                .id(1L)
                .authorities(UserRole.USER)
                .nickname("nickname")
                .password("pw")
                .email("test@gmail.com")
                .address(Fixture.createAddress())
                .rating(4)
                .build();
    }

    public static AlarmPayload createAlarmPayload() {
        return AlarmPayload.builder()
                .message("test message")
                .productId(1L)
                .memberId(123L)
                .fromMemberId(3L)
                .alarmType(AlarmType.TRANSACTION)
                .build();
    }

    public static MemberRequest createMemberRequest() {
        return new MemberRequest("user111", Fixture.createAddress());
    }

    public static ReviewRequest createReviewRequest(int rate) {
        return new ReviewRequest(rate);
    }

    public static AuctionRequest createAuctionRequest() {
        return new AuctionRequest(
                "title",
                "content",
                ProductCategory.BEAUTY,
                ProductStatus.GOOD,
                AuctionStatus.CANCEL,
                1000,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1));
    }

    public static BiddingRequest createBiddingRequest(int biddingPrice) {
        return new BiddingRequest(biddingPrice);
    }
}
