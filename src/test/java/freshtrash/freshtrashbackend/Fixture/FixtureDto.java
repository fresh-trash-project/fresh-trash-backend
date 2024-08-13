package freshtrash.freshtrashbackend.Fixture;

import freshtrash.freshtrashbackend.domain.alarm.dto.request.BaseAlarmPayload;
import freshtrash.freshtrashbackend.domain.alarm.dto.request.ProductAlarmPayload;
import freshtrash.freshtrashbackend.domain.alarm.dto.response.AlarmResponse;
import freshtrash.freshtrashbackend.domain.alarm.entity.AlarmArgs;
import freshtrash.freshtrashbackend.domain.alarm.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.domain.auction.dto.request.AuctionRequest;
import freshtrash.freshtrashbackend.domain.auction.dto.request.AuctionReviewRequest;
import freshtrash.freshtrashbackend.domain.auction.dto.request.BiddingRequest;
import freshtrash.freshtrashbackend.domain.auction.entity.constants.AuctionStatus;
import freshtrash.freshtrashbackend.domain.member.dto.request.ChangePasswordRequest;
import freshtrash.freshtrashbackend.domain.member.dto.request.LoginRequest;
import freshtrash.freshtrashbackend.domain.member.dto.request.MemberRequest;
import freshtrash.freshtrashbackend.domain.member.dto.request.SignUpRequest;
import freshtrash.freshtrashbackend.domain.member.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.domain.member.entity.Address;
import freshtrash.freshtrashbackend.domain.member.entity.constants.UserRole;
import freshtrash.freshtrashbackend.domain.product.dto.request.ProductRequest;
import freshtrash.freshtrashbackend.domain.product.dto.request.ProductReviewRequest;
import freshtrash.freshtrashbackend.domain.product.entity.constants.ProductCategory;
import freshtrash.freshtrashbackend.domain.product.entity.constants.ProductSellStatus;
import freshtrash.freshtrashbackend.domain.product.entity.constants.ProductStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public class FixtureDto {

    public static ProductRequest createProductRequest(
            String title,
            String content,
            ProductCategory productCategory,
            ProductStatus productStatus,
            ProductSellStatus productSellStatus,
            Integer productPrice,
            Address address) {
        return new ProductRequest(
                title,
                content,
                productCategory,
                productStatus,
                productSellStatus,
                productPrice,
                address.allBlank() ? null : address);
    }

    public static ProductRequest createProductRequest() {
        return new ProductRequest(
                "title",
                "content",
                ProductCategory.BEAUTY,
                ProductStatus.BEST,
                ProductSellStatus.CLOSE,
                0,
                Fixture.createAddress());
    }

    public static MemberPrincipal createMemberPrincipal() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return MemberPrincipal.builder()
                .id(1L)
                .authorities(UserRole.USER)
                .nickname("nickname")
                .password(encoder.encode("qwer1234!!"))
                .email("test@gmail.com")
                .address(Fixture.createAddress())
                .rating(4)
                .build();
    }

    public static BaseAlarmPayload createAlarmPayload() {
        return ProductAlarmPayload.builder()
                .message("test message")
                .targetId(1L)
                .memberId(123L)
                .fromMemberId(3L)
                .alarmType(AlarmType.COMPLETE_TRANSACTION)
                .build();
    }

    public static MemberRequest createMemberRequest() {
        return new MemberRequest("user111", Fixture.createAddress());
    }

    public static ProductReviewRequest createProductReviewRequest(int rate) {
        return new ProductReviewRequest(rate, "");
    }

    public static AuctionReviewRequest createAuctionReviewRequest(int rate, String content) {
        return new AuctionReviewRequest(rate, content);
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

    public static ChangePasswordRequest createChangePasswordRequest(String oldPassword, String newPassword) {
        return new ChangePasswordRequest(oldPassword, newPassword);
    }

    public static ProductAlarmPayload createProductAlarmPayload() {
        return ProductAlarmPayload.builder()
                .alarmType(AlarmType.COMPLETE_TRANSACTION)
                .fromMemberId(1L)
                .memberId(3L)
                .message("message")
                .targetId(2L)
                .build();
    }

    public static AlarmResponse createAlarmResponse() {
        return AlarmResponse.builder()
                .id(12L)
                .message("message")
                .alarmArgs(AlarmArgs.of(2L))
                .alarmType(AlarmType.COMPLETE_TRANSACTION)
                .readAt(LocalDateTime.now())
                .build();
    }

    public static SignUpRequest createSignUpRequest() {
        return new SignUpRequest("testUser", "testUser@gmail.com", "1234asdf@");
    }

    public static LoginRequest createLoginRequest() {
        return new LoginRequest("testUser@gmail.com", "1234asdr@");
    }
}
