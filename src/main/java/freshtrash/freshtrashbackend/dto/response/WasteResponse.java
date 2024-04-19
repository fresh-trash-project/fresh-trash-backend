package freshtrash.freshtrashbackend.dto.response;

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
public record WasteResponse(
        Long id,
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
        MemberResponse memberResponse) {

    public static class WasteResponseBuilder {
        public WasteResponseBuilder wastePrice(Integer wastePrice) {
            this.wastePrice = wastePrice;
            this.sellType = wastePrice == 0 ? SellType.SHARE : SellType.TRANSACTION;
            return this;
        }
    }

    public static WasteResponse fromEntity(Waste waste) {
        return WasteResponse.of(waste, MemberResponse.fromEntity(waste.getMember()));
    }

    public static WasteResponse fromEntity(Waste waste, MemberPrincipal memberPrincipal) {
        return WasteResponse.of(waste, MemberResponse.fromPrincipal(memberPrincipal));
    }

    private static WasteResponse of(Waste waste, MemberResponse memberResponse) {
        return WasteResponse.builder()
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
                .memberResponse(memberResponse)
                .build();
    }
}
