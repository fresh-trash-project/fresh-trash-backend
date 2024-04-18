package freshtrash.freshtrashbackend.dto.request;

import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.entity.constants.WasteCategory;
import freshtrash.freshtrashbackend.entity.constants.WasteStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record WasteRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotNull WasteCategory wasteCategory,
        @NotNull WasteStatus wasteStatus,
        @NotNull SellStatus sellStatus,
        @NotNull Integer wastePrice,
        @NotNull Address address) {}
