package freshtrash.freshtrashbackend.service;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.dto.request.ProductRequest;
import freshtrash.freshtrashbackend.dto.response.ProductResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Product;
import freshtrash.freshtrashbackend.entity.QProduct;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import freshtrash.freshtrashbackend.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private FileService fileService;

    @Test
    @DisplayName("Product 단일 조회")
    void given_productId_when_getProduct_then_productIsNotNull() {
        // given
        Long productId = 1L;
        given(productRepository.findById(eq(productId))).willReturn(Optional.of(Fixture.createProduct()));
        // when
        Product product = productService.getProduct(productId);
        // then
        assertThat(product).isNotNull();
    }

    @Test
    @DisplayName("Product 목록 조회")
    void given_predicateAndPageable_when_getProducts_then_productsSizeIsEqualToExpectedSize() {
        // given
        int expectedSize = 1;
        String district = "";
        Predicate predicate = QProduct.product.title.equalsIgnoreCase("title");
        Pageable pageable = PageRequest.of(0, 6);
        given(productRepository.findAll(eq(district), eq(predicate), eq(pageable)))
                .willReturn(new PageImpl<>(List.of(Fixture.createProduct())));
        // when
        Page<ProductResponse> products = productService.getProducts(district, predicate, pageable);
        // then
        assertThat(products.getSize()).isEqualTo(expectedSize);
    }

    @Test
    @DisplayName("Product 추가")
    void given_imgFileAndProductRequest_when_addProduct_then_productRequestValuesEqualsToSavedProductValues() {
        // given
        ProductRequest productRequest = FixtureDto.createProductRequest();
        MemberPrincipal memberPrincipal = FixtureDto.createMemberPrincipal();
        Product product = Product.fromRequest(productRequest, "test.png", memberPrincipal.id());
        given(productRepository.save(any(Product.class))).willReturn(product);
        willDoNothing().given(fileService).uploadFile(any(MultipartFile.class), anyString());
        // when
        ProductResponse productResponse =
                productService.addProduct(Fixture.createMultipartFileOfImage("image"), productRequest, memberPrincipal);
        // then
        assertThat(productResponse.title()).isEqualTo(productRequest.title());
        assertThat(productResponse.content()).isEqualTo(productRequest.content());
        assertThat(productResponse.productCategory()).isEqualTo(productRequest.productCategory());
        assertThat(productResponse.productStatus()).isEqualTo(productRequest.productStatus());
        assertThat(productResponse.sellStatus()).isEqualTo(productRequest.sellStatus());
        assertThat(productResponse.productPrice()).isEqualTo(productRequest.productPrice());
        assertThat(productResponse.address()).isEqualTo(productRequest.address());
    }

    @Test
    @DisplayName("Product 수정_파일이 유효한 경우_저장된 파일명은 이전에 저장한 파일명과 상이")
    void given_imgFileAndProductRequest_when_updateProduct_then_productRequestValuesEqualsToUpdatedProductValues() {
        // given
        Long productId = 1L;
        MockMultipartFile multipartFile = Fixture.createMultipartFileOfImage("test content");
        ProductRequest productRequest = FixtureDto.createProductRequest();
        String savedFileName = "saved.png";
        String updatedFileName = "updated.png";
        MemberPrincipal memberPrincipal = FixtureDto.createMemberPrincipal();
        Product product = Product.fromRequest(productRequest, updatedFileName, memberPrincipal.id());
        given(productRepository.existsByIdAndMember_Id(eq(productId), eq(memberPrincipal.id())))
                .willReturn(true);
        given(productRepository.save(any(Product.class))).willReturn(product);
        willDoNothing().given(fileService).uploadFile(any(MultipartFile.class), anyString());
        // when
        ProductResponse productResponse =
                productService.updateProduct(productId, multipartFile, productRequest, memberPrincipal);

        // then
        assertThat(productResponse.title()).isEqualTo(productRequest.title());
        assertThat(productResponse.content()).isEqualTo(productRequest.content());
        assertThat(productResponse.productCategory()).isEqualTo(productRequest.productCategory());
        assertThat(productResponse.productStatus()).isEqualTo(productRequest.productStatus());
        assertThat(productResponse.sellStatus()).isEqualTo(productRequest.sellStatus());
        assertThat(productResponse.productPrice()).isEqualTo(productRequest.productPrice());
        assertThat(productResponse.address()).isEqualTo(productRequest.address());
        assertThat(productResponse.fileName()).isNotEqualTo(savedFileName);
    }

    @Test
    @DisplayName("Product 삭제")
    void given_productId_when_then_deleteProductAndFile() {
        // given
        Long productId = 1L, memberId = 123L;
        UserRole userRole = UserRole.USER;
        given(productRepository.existsByIdAndMember_Id(eq(productId), eq(memberId)))
                .willReturn(true);
        willDoNothing().given(productRepository).deleteById(eq(productId));
        // when
        productService.deleteProduct(productId, userRole, memberId);
        // then
    }
}