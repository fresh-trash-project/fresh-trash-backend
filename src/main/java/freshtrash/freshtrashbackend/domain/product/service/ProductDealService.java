package freshtrash.freshtrashbackend.domain.product.service;

import freshtrash.freshtrashbackend.domain.chatRoom.entity.constants.ChatRoomSellStatus;
import freshtrash.freshtrashbackend.domain.product.controller.constants.ProductDealMemberType;
import freshtrash.freshtrashbackend.domain.product.dto.response.ProductResponse;
import freshtrash.freshtrashbackend.domain.product.entity.ProductDealLog;
import freshtrash.freshtrashbackend.domain.product.entity.constants.ProductSellStatus;
import freshtrash.freshtrashbackend.domain.chatRoom.repository.ChatRoomRepository;
import freshtrash.freshtrashbackend.domain.product.repository.ProductDealLogRepository;
import freshtrash.freshtrashbackend.domain.product.repository.ProductRepository;
import freshtrash.freshtrashbackend.global.infra.RecSysService;
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
    private final RecSysService recSysService;

    public Page<ProductResponse> getTransactedProducts(
            Long memberId, ProductDealMemberType memberType, Pageable pageable) {
        switch (memberType) {
            case SELLER_CLOSE -> {
                return productDealLogRepository
                        .findAllBySeller_Id(memberId, pageable)
                        .map(ProductDealLog::getProduct)
                        .map(ProductResponse::fromEntity);
            }
            case SELLER_ONGOING -> {
                return productRepository
                        .findAllByMemberIdAndSellStatusNot(memberId, ProductSellStatus.CLOSE, pageable)
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
    public void completeProductDeal(
            Long productId, Long chatRoomId, Long sellerId, Long buyerId, ProductSellStatus productSellStatus, ChatRoomSellStatus chatRoomSellStatus) {
        updateSellStatus(productId, chatRoomId, productSellStatus, chatRoomSellStatus);
        saveProductDealLog(productId, sellerId, buyerId);
        recSysService.purchaseProduct(productId, buyerId);
    }

    @Transactional
    public void updateSellStatus(Long productId, Long chatRoomId, ProductSellStatus productSellStatus, ChatRoomSellStatus chatRoomSellStatus) {
        productRepository.updateSellStatus(productId, productSellStatus);
        chatRoomRepository.updateSellStatus(chatRoomId, chatRoomSellStatus);
    }

    private void saveProductDealLog(Long productId, Long sellerId, Long buyerId) {
        productDealLogRepository.save(ProductDealLog.builder()
                .productId(productId)
                .sellerId(sellerId)
                .buyerId(buyerId)
                .build());
    }
}
