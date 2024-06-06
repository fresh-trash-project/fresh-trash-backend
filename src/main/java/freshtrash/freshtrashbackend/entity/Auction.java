package freshtrash.freshtrashbackend.entity;

import freshtrash.freshtrashbackend.dto.request.AuctionRequest;
import freshtrash.freshtrashbackend.entity.audit.CreatedAt;
import freshtrash.freshtrashbackend.entity.constants.AuctionStatus;
import freshtrash.freshtrashbackend.entity.constants.ProductCategory;
import freshtrash.freshtrashbackend.entity.constants.ProductStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
@Table(name = "auctions")
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Auction extends CreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Setter
    @Column(nullable = false)
    private String title;

    @Setter
    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(nullable = false)
    private int viewCount;

    @Setter
    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ProductCategory productCategory;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ProductStatus productStatus;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AuctionStatus auctionStatus;

    @Setter
    @Column(nullable = false)
    private int finalBid; // 최종 입찰 금액

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime endedAt;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = LAZY)
    @JoinColumn(name = "memberId", insertable = false, updatable = false)
    private Member member;

    @Column(nullable = false)
    private Long memberId;

    @Version
    private int version;

    @Builder
    public Auction(
            String title,
            String content,
            String fileName,
            ProductCategory productCategory,
            ProductStatus productStatus,
            AuctionStatus auctionStatus,
            int finalBid,
            LocalDateTime startedAt,
            LocalDateTime endedAt,
            Long memberId) {
        this.title = title;
        this.content = content;
        this.fileName = fileName;
        this.productCategory = productCategory;
        this.productStatus = productStatus;
        this.auctionStatus = auctionStatus;
        this.finalBid = finalBid;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.memberId = memberId;
    }

    public static Auction fromRequest(AuctionRequest auctionRequest, String fileName, Long memberId) {
        return Auction.builder()
                .title(auctionRequest.title())
                .content(auctionRequest.content())
                .productCategory(auctionRequest.productCategory())
                .productStatus(auctionRequest.productStatus())
                .auctionStatus(auctionRequest.auctionStatus())
                .finalBid(auctionRequest.finalBid())
                .startedAt(auctionRequest.startedAt())
                .endedAt(auctionRequest.endedAt())
                .fileName(fileName)
                .memberId(memberId)
                .build();
    }
}
