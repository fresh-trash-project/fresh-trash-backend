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
        if (productLikeRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new ProductException(ErrorCode.ALREADY_EXISTS_LIKE);
        }

        productLikeRepository.save(ProductLike.of(memberId, productId));
        productRepository.updateLikeCount(productId, 1);
    }

    @Transactional
    public void deleteProductLike(Long memberId, Long productId) {
        if (!productLikeRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new ProductException(ErrorCode.NOT_FOUND_LIKE);
        }

        productLikeRepository.deleteByMemberIdAndProductId(memberId, productId);
        productRepository.updateLikeCount(productId, -1);
    }
}
