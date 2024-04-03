package freshtrash.freshtrashbackend.dto;

import freshtrash.freshtrashbackend.dto.constants.SellType;
import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.entity.constants.WasteCategory;
import freshtrash.freshtrashbackend.entity.constants.WasteStatus;

import java.time.LocalDateTime;

public record WasteDto(
        String title,
        String content,
        SellType sellType,
        Integer wastePrice,
        Integer likeCount,
        Integer viewCount,
        String fileName,
        WasteCategory wasteCategory,
        WasteStatus wasteStatus,
        SellStatus sellStatus,
        Address address,
        LocalDateTime createdAt) {
    public static WasteDto fromEntity(Waste waste) {
        return new WasteDto(
                waste.getTitle(),
                waste.getContent(),
                waste.getWastePrice() == 0 ? SellType.SHARE : SellType.TRANSACTION,
                waste.getWastePrice(),
                waste.getLikeCount(),
                waste.getViewCount(),
                waste.getFileName(),
                waste.getWasteCategory(),
                waste.getWasteStatus(),
                waste.getSellStatus(),
                waste.getAddress(),
                waste.getCreatedAt());
    }
}
