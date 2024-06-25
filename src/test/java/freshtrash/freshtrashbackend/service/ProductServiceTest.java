package freshtrash.freshtrashbackend.service;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.dto.projections.FileNameSummary;
import freshtrash.freshtrashbackend.dto.request.ProductRequest;
import freshtrash.freshtrashbackend.dto.response.ProductResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Product;
import freshtrash.freshtrashbackend.entity.QProduct;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import freshtrash.freshtrashbackend.exception.FileException;
import freshtrash.freshtrashbackend.exception.ProductException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
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

import static org.assertj.core.api.Assertions.*;
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
    @DisplayName("Product 수정할 때 작성자와 admin이 아니라면 예외가 발생한다.")
    void given_imgFileAndProductRequest_when_notAdminAndWriter_then_throwException() {
        // given
        Long productId = 1L;
        MockMultipartFile multipartFile = Fixture.createMultipartFileOfImage("test content");
        ProductRequest productRequest = FixtureDto.createProductRequest();
        MemberPrincipal memberPrincipal = FixtureDto.createMemberPrincipal();
        given(productRepository.existsByIdAndMember_Id(eq(productId), eq(memberPrincipal.id())))
                .willReturn(false);
        // when
        assertThatThrownBy(
                        () -> productService.updateProduct(productId, multipartFile, productRequest, memberPrincipal))
                .isInstanceOf(ProductException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_PRODUCT);
        // then
    }

    @Test
    @DisplayName("Product 수정할 때 입력받은 file이 유효하지 않을 경우 예외를 발생시킨다.")
    void given_imgFileAndProductRequest_when_invalidFile_then_throwException() {
        // given
        Long productId = 1L;
        MockMultipartFile multipartFile = null;
        ProductRequest productRequest = FixtureDto.createProductRequest();
        MemberPrincipal memberPrincipal = FixtureDto.createMemberPrincipal();
        given(productRepository.existsByIdAndMember_Id(eq(productId), eq(memberPrincipal.id())))
                .willReturn(true);
        // when
        assertThatThrownBy(
                        () -> productService.updateProduct(productId, multipartFile, productRequest, memberPrincipal))
                .isInstanceOf(FileException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_FIlE);
        // then
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

    @Test
    @DisplayName("productId를 받아 fileName만 조회한다.")
    void given_productId_when_then_returnFileName() {
        // given
        Long productId = 1L;
        String fileName = "file";
        given(productRepository.findFileNameById(productId)).willReturn(Optional.of(new FileNameSummary(fileName)));
        // when
        FileNameSummary fileNameSummary = productService.findFileNameOfProduct(productId);
        // then
        assertThat(fileNameSummary.fileName()).isEqualTo(fileName);
    }

    @Test
    @DisplayName("productId를 입력받으면 해당 중고 상품의 조회 수를 +1 업데이트한다.")
    void given_productId_when_then_viewCountPlus1() {
        // given
        Long productId = 1L;
        willDoNothing().given(productRepository).updateViewCount(productId);
        // when
        assertThatCode(() -> productService.updateViewCount(productId)).doesNotThrowAnyException();
        // then
    }
}