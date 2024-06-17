package freshtrash.freshtrashbackend.controller;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.dto.request.AuctionRequest;
import freshtrash.freshtrashbackend.dto.request.BiddingRequest;
import freshtrash.freshtrashbackend.dto.response.AuctionResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.service.AuctionEventService;
import freshtrash.freshtrashbackend.service.AuctionService;
import freshtrash.freshtrashbackend.service.BiddingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auctions")
public class AuctionApi {
    private final AuctionService auctionService;
    private final AuctionEventService auctionEventService;
    private final BiddingHistoryService biddingHistoryService;

    @GetMapping
    public ResponseEntity<Page<AuctionResponse>> getAuctions(
            @QuerydslPredicate(root = Auction.class) Predicate predicate,
            @PageableDefault(size = 6, sort = "createdAt", direction = DESC) Pageable pageable) {
        Page<AuctionResponse> auctions = auctionService.getAuctions(predicate, pageable);
        return ResponseEntity.ok(auctions);
    }

    @GetMapping("/{auctionId}")
    public ResponseEntity<AuctionResponse> getAuction(@PathVariable Long auctionId) {
        AuctionResponse auctionResponse = AuctionResponse.fromEntity(auctionService.getAuction(auctionId));
        return ResponseEntity.ok(auctionResponse);
    }

    @PostMapping
    public ResponseEntity<AuctionResponse> addAuction(
            @RequestPart MultipartFile imgFile,
            @RequestPart @Valid AuctionRequest auctionRequest,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        AuctionResponse auctionResponse = auctionService.addAuction(imgFile, auctionRequest, memberPrincipal);
        return ResponseEntity.status(HttpStatus.CREATED).body(auctionResponse);
    }

    @DeleteMapping("/{auctionId}")
    public ResponseEntity<Void> cancelAuction(
            @PathVariable Long auctionId, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        auctionEventService.cancelAuction(auctionId, memberPrincipal.getUserRole(), memberPrincipal.id());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PutMapping("/{auctionId}/bid")
    public ResponseEntity<Void> placeBidding(
            @PathVariable Long auctionId,
            @RequestBody @Valid BiddingRequest biddingRequest,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        auctionService.requestBidding(auctionId, biddingRequest.biddingPrice(), memberPrincipal.id());
        return ResponseEntity.ok(null);
    }

    @PutMapping("/{auctionId}/pay")
    public ResponseEntity<Void> completePay(
            @PathVariable Long auctionId, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        biddingHistoryService.updateToCompletedPayAndNotify(auctionId, memberPrincipal.id());
        return ResponseEntity.ok(null);
    }
}
