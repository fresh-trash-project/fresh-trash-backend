package freshtrash.freshtrashbackend.Fixture;

import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import freshtrash.freshtrashbackend.dto.response.WasteResponse;
import freshtrash.freshtrashbackend.dto.constants.SellType;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import freshtrash.freshtrashbackend.entity.constants.WasteCategory;
import freshtrash.freshtrashbackend.entity.constants.WasteStatus;

import java.time.LocalDateTime;

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
}
