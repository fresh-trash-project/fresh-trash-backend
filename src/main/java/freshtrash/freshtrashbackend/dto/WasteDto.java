package freshtrash.freshtrashbackend.dto;

import freshtrash.freshtrashbackend.dto.constants.SellType;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.entity.constants.WasteCategory;
import freshtrash.freshtrashbackend.entity.constants.WasteStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
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
        LocalDateTime createdAt,
        UserInfo userInfo) {
    public static class WasteDtoBuilder {
        public WasteDtoBuilder wastePrice(Integer wastePrice) {
            this.wastePrice = wastePrice;
            this.sellType = wastePrice == 0 ? SellType.SHARE : SellType.TRANSACTION;
            return this;
        }
    }

    public static WasteDto fromEntity(Waste waste) {
        return WasteDto.of(waste, UserInfo.fromEntity(waste.getMember()));
    }

    public static WasteDto fromEntity(Waste waste, MemberPrincipal memberPrincipal) {
        return WasteDto.of(waste, UserInfo.fromPrincipal(memberPrincipal));
    }

    private static WasteDto of(Waste waste, UserInfo userInfo) {
        return WasteDto.builder()
                .title(waste.getTitle())
                .content(waste.getContent())
                .wastePrice(waste.getWastePrice())
                .likeCount(waste.getLikeCount())
                .viewCount(waste.getViewCount())
                .fileName(waste.getFileName())
                .wasteCategory(waste.getWasteCategory())
                .wasteStatus(waste.getWasteStatus())
                .sellStatus(waste.getSellStatus())
                .address(waste.getAddress())
                .createdAt(waste.getCreatedAt())
                .userInfo(userInfo)
                .build();
    }
}
