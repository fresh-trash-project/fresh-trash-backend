package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.controller.constants.ProductDealMemberType;
import freshtrash.freshtrashbackend.dto.response.ProductResponse;
import freshtrash.freshtrashbackend.entity.ProductDealLog;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.repository.ChatRoomRepository;
import freshtrash.freshtrashbackend.repository.ProductDealLogRepository;
import freshtrash.freshtrashbackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductDealService {
    private final ProductDealLogRepository productDealLogRepository;
    private final ProductRepository productRepository;
    private final ChatRoomRepository chatRoomRepository;

    public Page<ProductResponse> getTransactedProducts(Long memberId, ProductDealMemberType memberType, Pageable pageable) {
        switch (memberType) {
            case SELLER_CLOSE -> {
                return productDealLogRepository
                        .findAllBySeller_Id(memberId, pageable)
                        .map(ProductDealLog::getProduct)
                        .map(ProductResponse::fromEntity);
            }
            case SELLER_ONGOING -> {
                return productRepository
                        .findAllByMemberIdAndSellStatusNot(memberId, SellStatus.CLOSE, pageable)
                        .map(ProductResponse::fromEntity);
            }
            default -> {
                return productDealLogRepository
                        .findAllByBuyer_Id(memberId, pageable)
                        .map(ProductDealLog::getProduct)
                        .map(ProductResponse::fromEntity);
            }
        }
    }

    /**
     * - 폐기물과 채팅방의 판매 상태 변경
     * - 거래 내역 저장
     */
    @Transactional
    public void completeProductDeal(Long productId, Long chatRoomId, Long sellerId, Long buyerId, SellStatus sellStatus) {
        updateSellStatus(productId, chatRoomId, sellStatus);
        saveProductDealLog(productId, sellerId, buyerId);
    }

    @Transactional
    public void updateSellStatus(Long productId, Long chatRoomId, SellStatus sellStatus) {
        productRepository.updateSellStatus(productId, sellStatus);
        chatRoomRepository.updateSellStatus(chatRoomId, sellStatus);
    }

    private void saveProductDealLog(Long productId, Long sellerId, Long buyerId) {
        productDealLogRepository.save(ProductDealLog.builder()
                .productId(productId)
                .sellerId(sellerId)
                .buyerId(buyerId)
                .build());
    }
}
