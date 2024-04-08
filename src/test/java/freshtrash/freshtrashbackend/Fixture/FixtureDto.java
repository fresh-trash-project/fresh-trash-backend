package freshtrash.freshtrashbackend.Fixture;

import freshtrash.freshtrashbackend.dto.UserInfo;
import freshtrash.freshtrashbackend.dto.WasteDto;
import freshtrash.freshtrashbackend.dto.constants.SellType;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
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

    public static WasteDto createWasteDto() {
        return new WasteDto(
                "title",
                "content",
                SellType.SHARE,
                1000,
                2,
                3,
                "test.png",
                WasteCategory.BEAUTY,
                WasteStatus.BEST,
                SellStatus.CLOSE,
                Fixture.createAddress(),
                LocalDateTime.now(),
                new UserInfo("test", 4, "test.png", Fixture.createAddress()));
    }
}
