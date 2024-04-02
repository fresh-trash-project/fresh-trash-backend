package freshtrash.freshtrashbackend.Fixture;

import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.entity.constants.WasteCategory;
import freshtrash.freshtrashbackend.entity.constants.WasteStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

public class Fixture {
    public static Waste createWaste() {
        return Waste.of(
                "title",
                "content",
                0,
                "test.png",
                WasteCategory.BEAUTY,
                WasteStatus.BEST,
                SellStatus.CLOSE,
                Address.of("12345", "state", "city", "district", "detail"));
    }

    public static MockMultipartFile createMultipartFile(String content) {
        String fileName = "test.png";
        return new MockMultipartFile("modelFile", fileName, "text/plain", content.getBytes(StandardCharsets.UTF_8));
    }
}
