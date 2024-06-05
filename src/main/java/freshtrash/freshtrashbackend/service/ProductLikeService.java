package freshtrash.freshtrashbackend.service;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.dto.response.ProductResponse;
import freshtrash.freshtrashbackend.entity.ProductLike;
import freshtrash.freshtrashbackend.exception.ProductException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.ProductLikeRepository;
import freshtrash.freshtrashbackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductLikeService {
    private final ProductLikeRepository productLikeRepository;
    private final ProductRepository productRepository;

    public Page<ProductResponse> getLikedProducts(Predicate predicate, Pageable pageable) {
        return productLikeRepository
                .findAll(predicate, pageable)
                .map(ProductLike::getProduct)
                .map(ProductResponse::fromEntity);
    }

    @Transactional
    public void addProductLike(Long memberId, Long productId) {
        checkIfNotWriterForLike(productId, memberId);
        if (productLikeRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new ProductException(ErrorCode.ALREADY_EXISTS_LIKE);
        }

        productLikeRepository.save(ProductLike.of(memberId, productId));
        productRepository.updateLikeCount(productId, 1);
    }

    @Transactional
    public void deleteProductLike(Long memberId, Long productId) {
        checkIfNotWriterForLike(productId, memberId);
        if (!productLikeRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new ProductException(ErrorCode.NOT_FOUND_LIKE);
        }

        productLikeRepository.deleteByMemberIdAndProductId(memberId, productId);
        productRepository.updateLikeCount(productId, -1);
    }

    /**
     * 작성자는 좋아요를 추가/삭제 할 수 없음
     */
    public void checkIfNotWriterForLike(Long productId, Long memberId) {
        if (productRepository.existsByIdAndMember_Id(productId, memberId))
            throw new ProductException(ErrorCode.OWNER_PRODUCT_CANT_LIKE);
    }
}
