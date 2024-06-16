package freshtrash.freshtrashbackend.service;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.dto.request.AuctionRequest;
import freshtrash.freshtrashbackend.dto.response.AuctionResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.entity.BiddingHistory;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import freshtrash.freshtrashbackend.exception.AuctionException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.AuctionRepository;
import freshtrash.freshtrashbackend.repository.BiddingHistoryRepository;
import freshtrash.freshtrashbackend.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionService {
    private final BiddingHistoryRepository biddingHistoryRepository;
    private final AuctionRepository auctionRepository;
    private final FileService fileService;

    public AuctionResponse addAuction(
            MultipartFile imgFile, AuctionRequest auctionRequest, MemberPrincipal memberPrincipal) {
        String savedFileName = FileUtils.generateUniqueFileName(imgFile);
        Auction auction = Auction.fromRequest(auctionRequest, savedFileName, memberPrincipal.id());

        Auction savedAuction = auctionRepository.save(auction);
        // 이미지 파일 저장
        fileService.uploadFile(imgFile, savedFileName);
        return AuctionResponse.fromEntity(savedAuction, memberPrincipal);
    }

    public Page<AuctionResponse> getAuctions(Predicate predicate, Pageable pageable) {
        return auctionRepository.findAll(predicate, pageable).map(AuctionResponse::fromEntity);
    }

    public Auction getAuction(Long auctionId) {
        return auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new AuctionException(ErrorCode.NOT_FOUND_AUCTION));
    }

    public void deleteAuction(Long auctionId, UserRole userRole, Long memberId) {
        checkIfWriterOrAdmin(auctionId, userRole, memberId);
        auctionRepository.deleteById(auctionId);
    }

    @Transactional
    @Retryable(
            value = {ObjectOptimisticLockingFailureException.class, CannotAcquireLockException.class},
            backoff = @Backoff(delay = 300, maxDelay = 700))
    public void requestBidding(Long auctionId, int biddingPrice, Long memberId) {
        Auction auction = getAuction(auctionId);

        // 통합 테스트를 위해 추가된 코드
        if (log.isDebugEnabled()) {
            try {
                Random random = new Random();
                long ms = random.nextLong(100) + 200;
                log.debug("sleep... {}ms", ms);
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        validateBiddingRequest(auction, biddingPrice, memberId);
        // 입찰가 변경
        auction.setFinalBid(biddingPrice);
        // 입찰 기록
        addBiddingHistory(auctionId, memberId, biddingPrice);
    }

    public void closeAuction(Long auctionId) {
        log.debug("경매 판매 상태를 CLOSE로 변경");
        auctionRepository.closeAuctionById(auctionId);
    }

    public List<Auction> getEndedAuctions() {
        log.debug("마감되었지만 AuctionStatus가 ONGOING인 경매 조회");
        return auctionRepository.findAllEndedAuctions();
    }

    private void validateBiddingRequest(Auction auction, int biddingPrice, Long memberId) {
        // 요청한 입찰가는 이전 입찰가보다 높아야함
        log.debug("Read finalBid -> {}, Bid Price -> {}", auction.getFinalBid(), biddingPrice);
        if (auction.getFinalBid() >= biddingPrice || biddingPrice % 10 != 0) {
            throw new AuctionException(ErrorCode.INVALID_BIDDING_PRICE);
        }
        // 경매를 올린 사용자는 입찰이 불가능
        if (Objects.equals(auction.getMemberId(), memberId)) {
            throw new AuctionException(ErrorCode.WRITER_CANT_BIDDING);
        }
        // 경매가 시작하기 전 또는 후에는 입찰이 불가능
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(auction.getStartedAt()) || now.isAfter(auction.getEndedAt())) {
            throw new AuctionException(ErrorCode.CANT_BIDDING_TIME);
        }
    }

    private void checkIfWriterOrAdmin(Long auctionId, UserRole userRole, Long memberId) {
        if (userRole != UserRole.ADMIN && !auctionRepository.existsByIdAndMemberId(auctionId, memberId))
            throw new AuctionException(ErrorCode.FORBIDDEN_AUCTION);
    }

    private void addBiddingHistory(Long auctionId, Long memberId, int price) {
        biddingHistoryRepository.save(BiddingHistory.builder()
                .auctionId(auctionId)
                .memberId(memberId)
                .price(price)
                .build());
    }
}
