package freshtrash.freshtrashbackend.Fixture;

import freshtrash.freshtrashbackend.dto.request.AlarmPayload;
import freshtrash.freshtrashbackend.dto.request.MemberRequest;
import freshtrash.freshtrashbackend.dto.request.ReviewRequest;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.constants.*;

public class FixtureDto {

    public static WasteRequest createWasteRequest(
            String title,
            String content,
            WasteCategory wasteCategory,
            WasteStatus wasteStatus,
            SellStatus sellStatus,
            Integer wastePrice,
            Address address) {
        return new WasteRequest(
                title,
                content,
                wasteCategory,
                wasteStatus,
                sellStatus,
                wastePrice,
                address.allBlank() ? null : address);
    }

    public static WasteRequest createWasteRequest() {
        return new WasteRequest(
                "title",
                "content",
                WasteCategory.BEAUTY,
                WasteStatus.BEST,
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
                .wasteId(1L)
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
}

