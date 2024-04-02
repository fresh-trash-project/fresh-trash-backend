package freshtrash.freshtrashbackend.Fixture;

import freshtrash.freshtrashbackend.dto.WasteDto;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.entity.constants.WasteCategory;
import freshtrash.freshtrashbackend.entity.constants.WasteStatus;

import java.time.LocalDateTime;

public class FixtureDto {

    public static WasteRequest createWasteRequest() {
        return new WasteRequest(
                "title",
                "content",
                WasteCategory.BEAUTY,
                WasteStatus.BEST,
                SellStatus.CLOSE,
                0,
                Address.of("12345", "state", "city", "district", "detail"));
    }

    public static WasteDto createWasteDto() {
        return new WasteDto(
                "title",
                "content",
                0,
                0,
                0,
                "test.png",
                WasteCategory.BEAUTY,
                WasteStatus.BEST,
                SellStatus.CLOSE,
                Address.of("12345", "state", "city", "district", "detail"),
                LocalDateTime.now());
    }
}
