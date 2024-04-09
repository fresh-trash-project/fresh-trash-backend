package freshtrash.freshtrashbackend.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.entity.constants.WasteCategory;
import freshtrash.freshtrashbackend.entity.constants.WasteStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

public record WasteRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotNull WasteCategory wasteCategory,
        @NotNull WasteStatus wasteStatus,
        @NotNull SellStatus sellStatus,
        @NotNull Integer wastePrice,
        @NotNull Address address) {
    public Waste toEntity(String fileName, Long memberId) {
        return Waste.builder()
                .title(title)
                .content(content)
                .wastePrice(wastePrice)
                .fileName(fileName)
                .wasteCategory(wasteCategory)
                .wasteStatus(wasteStatus)
                .sellStatus(sellStatus)
                .address(address)
                .memberId(memberId)
                .build();
    }
}
