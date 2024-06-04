package freshtrash.freshtrashbackend.service;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.dto.request.AuctionRequest;
import freshtrash.freshtrashbackend.dto.response.AuctionResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.exception.AuctionException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.AuctionRepository;
import freshtrash.freshtrashbackend.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AuctionService {
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

    public void deleteAuction(Long auctionId) {
        auctionRepository.deleteById(auctionId);
    }

    public boolean isWriterOfAuction(Long auctionId, Long memberId) {
        return auctionRepository.existsByIdAndMemberId(auctionId, memberId);
    }
}
