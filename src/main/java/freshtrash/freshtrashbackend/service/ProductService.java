package freshtrash.freshtrashbackend.service;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.dto.request.ProductRequest;
import freshtrash.freshtrashbackend.dto.response.ProductResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Product;
import freshtrash.freshtrashbackend.exception.FileException;
import freshtrash.freshtrashbackend.exception.ProductException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.ProductRepository;
import freshtrash.freshtrashbackend.repository.projections.FileNameSummary;
import freshtrash.freshtrashbackend.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final FileService fileService;

    public Product getProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new ProductException(ErrorCode.NOT_FOUND_PRODUCT));
    }

    public Page<ProductResponse> getProducts(String district, Predicate predicate, Pageable pageable) {
        return productRepository.findAll(district, predicate, pageable).map(ProductResponse::fromEntity);
    }

    @Transactional
    public ProductResponse addProduct(MultipartFile imgFile, ProductRequest productRequest, MemberPrincipal memberPrincipal) {
        // 주소가 입력되지 않았을 경우
        if (Objects.isNull(productRequest.address())) throw new ProductException(ErrorCode.EMPTY_ADDRESS);
        String savedFileName = FileUtils.generateUniqueFileName(imgFile);
        Product product = Product.fromRequest(productRequest, savedFileName, memberPrincipal.id());

        Product savedProduct = productRepository.save(product);
        // 이미지 파일 저장
        fileService.uploadFile(imgFile, savedFileName);
        return ProductResponse.fromEntity(savedProduct, memberPrincipal);
    }

    @Transactional
    public ProductResponse updateProduct(
            Long productId, MultipartFile imgFile, ProductRequest productRequest, MemberPrincipal memberPrincipal) {

        if (!FileUtils.isValid(imgFile)) {
            throw new FileException(ErrorCode.INVALID_FIlE);
        }

        // DB 업데이트
        String updatedFileName = FileUtils.generateUniqueFileName(imgFile);
        Product updatedProduct = Product.fromRequest(productRequest, updatedFileName, memberPrincipal.id());
        updatedProduct.setId(productId);
        productRepository.save(updatedProduct);
        // 수정된 파일 저장
        fileService.uploadFile(imgFile, updatedFileName);

        return ProductResponse.fromEntity(updatedProduct, memberPrincipal);
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    public FileNameSummary findFileNameOfProduct(Long productId) {
        return productRepository
                .findFileNameById(productId)
                .orElseThrow(() -> new ProductException(ErrorCode.NOT_FOUND_PRODUCT));
    }

    /**
     * 작성자인지 확인
     */
    public boolean isWriterOfArticle(Long productId, Long memberId) {
        return productRepository.existsByIdAndMember_Id(productId, memberId);
    }

    public void updateViewCount(Long productId) {
        productRepository.updateViewCount(productId);
    }
}