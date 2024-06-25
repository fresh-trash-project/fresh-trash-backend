package freshtrash.freshtrashbackend.Fixture;

import freshtrash.freshtrashbackend.dto.request.*;
import freshtrash.freshtrashbackend.dto.response.AlarmResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.AlarmArgs;
import freshtrash.freshtrashbackend.entity.constants.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
                .alarmType(AlarmType.TRANSACTION)
                .build();
    }

    public static MemberRequest createMemberRequest() {
        return new MemberRequest("user111", Fixture.createAddress());
    }

    public static ReviewRequest createReviewRequest(int rate) {
        return new ReviewRequest(rate, "");
    }

    public static ReviewRequest createReviewRequest(int rate, String content) {
        return new ReviewRequest(rate, content);
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
                .alarmType(AlarmType.TRANSACTION)
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
                .alarmType(AlarmType.TRANSACTION)
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
