package freshtrash.freshtrashbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.dto.request.ProductRequest;
import freshtrash.freshtrashbackend.dto.response.ProductResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.Product;
import freshtrash.freshtrashbackend.entity.constants.ProductCategory;
import freshtrash.freshtrashbackend.entity.constants.ProductStatus;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import freshtrash.freshtrashbackend.dto.projections.FileNameSummary;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.LocalFileService;
import freshtrash.freshtrashbackend.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.context.support.TestExecutionEvent.TEST_EXECUTION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductApi.class)
@Import(TestSecurityConfig.class)
class ProductApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private LocalFileService localFileService;

    @MockBean
    private ChatRoomService chatRoomService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("폐기물 단일 조회")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    void given_productId_when_getProduct_then_returnProductData() throws Exception {
        // given
        Long productId = 1L;
        Product product = Fixture.createProduct();
        given(productService.getProduct(eq(productId))).willReturn(product);
        // when
        mvc.perform(get("/api/v1/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(product.getTitle()))
                .andExpect(jsonPath("$.content").value(product.getContent()))
                .andExpect(jsonPath("$.productPrice").value(product.getProductPrice()))
                .andExpect(jsonPath("$.fileName").value(product.getFileName()))
                .andExpect(jsonPath("$.likeCount").value(product.getLikeCount()))
                .andExpect(jsonPath("$.viewCount").value(product.getViewCount()))
                .andExpect(jsonPath("$.productCategory")
                        .value(product.getProductCategory().name()))
                .andExpect(jsonPath("$.productStatus")
                        .value(product.getProductStatus().name()))
                .andExpect(
                        jsonPath("$.sellStatus").value(product.getSellStatus().name()));
        // then
    }

    @Test
    @DisplayName("페기물 목록 조회")
    void given_predicateAndPageable_when_getProducts_then_returnPagingProductData() throws Exception {
        // given
        given(productService.getProducts(eq(null), any(Predicate.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(ProductResponse.fromEntity(Fixture.createProduct()))));
        // when
        mvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(1));
        // then
    }

    @Test
    @DisplayName("폐기물 등록")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    void given_imgFileAndProductRequest_when_requestAddProduct_then_requestValuesEqualsToReturnedProductValues()
            throws Exception {
        Long memberId = 1L;
        // given
        MockMultipartFile imgFile = Fixture.createMultipartFileOfImage("test_image");
        ProductRequest productRequest = FixtureDto.createProductRequest();
        Product product = Product.fromRequest(productRequest, imgFile.getOriginalFilename(), memberId);
        ReflectionTestUtils.setField(product, "member", Fixture.createMember());
        ProductResponse productResponse = ProductResponse.fromEntity(product);
        given(productService.addProduct(
                        any(MultipartFile.class), any(ProductRequest.class), any(MemberPrincipal.class)))
                .willReturn(productResponse);
        // when
        mvc.perform(multipart(HttpMethod.POST, "/api/v1/products")
                        .file("imgFile", imgFile.getBytes())
                        .file(Fixture.createMultipartFileOfJson(
                                "productRequest", objectMapper.writeValueAsString(productRequest)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(productRequest.title()))
                .andExpect(jsonPath("$.content").value(productRequest.content()))
                .andExpect(jsonPath("$.productPrice").value(productRequest.productPrice()))
                .andExpect(jsonPath("$.fileName").value(imgFile.getOriginalFilename()))
                .andExpect(jsonPath("$.productCategory")
                        .value(productRequest.productCategory().name()))
                .andExpect(jsonPath("$.productStatus")
                        .value(productRequest.productStatus().name()))
                .andExpect(jsonPath("$.sellStatus")
                        .value(productRequest.sellStatus().name()));
        // then
    }

    @ParameterizedTest
    @DisplayName("어느 하나라도 입력되지 않았을 경우 폐기물 등록 실패")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    @CsvSource(
            value = {
                ", content, CLOTHING, BEST, CLOSE, 0, 12345, state, city, district, detail",
                "title, , CLOTHING, BEST, CLOSE, 0, 12345, state, city, district, detail",
                "title, content, , BEST, CLOSE, 0, 12345, state, city, district, detail",
                "title, content, CLOTHING, , CLOSE, 0, 12345, state, city, district, detail",
                "title, content, CLOTHING, BEST, , 0, 12345, state, city, district, detail",
                "title, content, CLOTHING, BEST, CLOSE, , 12345, state, city, district, detail",
                "title, content, CLOTHING, BEST, CLOSE, 0, , , , , ",
            })
    void given_missingOneInImgFileAndProductRequest_when_requestAddProduct_then_failed(
            String title,
            String content,
            ProductCategory productCategory,
            ProductStatus productStatus,
            SellStatus sellStatus,
            Integer productPrice,
            String zipcode,
            String state,
            String city,
            String district,
            String detail)
            throws Exception {
        // given
        MockMultipartFile imgFile = Fixture.createMultipartFileOfImage("test_image");
        ProductRequest productRequest = FixtureDto.createProductRequest(
                title,
                content,
                productCategory,
                productStatus,
                sellStatus,
                productPrice,
                Address.builder()
                        .zipcode(zipcode)
                        .state(state)
                        .city(city)
                        .district(district)
                        .detail(detail)
                        .build());
        // when
        mvc.perform(multipart(HttpMethod.POST, "/api/v1/products")
                        .file("imgFile", imgFile.getBytes())
                        .file(new MockMultipartFile(
                                "productRequest",
                                "",
                                "application/json",
                                objectMapper.writeValueAsBytes(productRequest)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        // then
    }

    @Test
    @DisplayName("폐기물 수정")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    void given_imgFileAndProductRequest_when_requestUpdateProduct_then_requestValuesEqualsToReturnedProductValues()
            throws Exception {
        // given
        Long productId = 1L, memberId = 123L;
        String fileName = "test.png";
        MockMultipartFile imgFile = Fixture.createMultipartFileOfImage("test_image");
        ProductRequest productRequest = FixtureDto.createProductRequest();
        Product product = Product.fromRequest(productRequest, imgFile.getOriginalFilename(), memberId);
        ReflectionTestUtils.setField(product, "member", Fixture.createMember());
        ProductResponse productResponse = ProductResponse.fromEntity(product);
        given(productService.findFileNameOfProduct(eq(productId))).willReturn(new FileNameSummary(fileName));
        given(productService.updateProduct(
                        eq(productId), any(MultipartFile.class), any(ProductRequest.class), any(MemberPrincipal.class)))
                .willReturn(productResponse);
        willDoNothing().given(localFileService).deleteFileIfExists(eq(fileName));
        // when
        mvc.perform(multipart(HttpMethod.PUT, "/api/v1/products/" + productId)
                        .file("imgFile", imgFile.getBytes())
                        .file(new MockMultipartFile(
                                "productRequest",
                                "",
                                "application/json",
                                objectMapper.writeValueAsBytes(productRequest)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(productRequest.title()))
                .andExpect(jsonPath("$.content").value(productRequest.content()))
                .andExpect(jsonPath("$.productPrice").value(productRequest.productPrice()))
                .andExpect(jsonPath("$.fileName").value(imgFile.getOriginalFilename()))
                .andExpect(jsonPath("$.productCategory")
                        .value(productRequest.productCategory().name()))
                .andExpect(jsonPath("$.productStatus")
                        .value(productRequest.productStatus().name()))
                .andExpect(jsonPath("$.sellStatus")
                        .value(productRequest.sellStatus().name()));
        // then
    }

    @Test
    @DisplayName("페기물 삭제")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    void given_productIdAndWriter_when_then_deleteProductAndFile() throws Exception {
        // given
        Long productId = 1L, memberId = 123L;
        UserRole userRole = UserRole.USER;
        String fileName = "test.png";
        given(productService.findFileNameOfProduct(eq(productId))).willReturn(new FileNameSummary(fileName));
        willDoNothing().given(localFileService).deleteFileIfExists(eq(fileName));
        willDoNothing().given(productService).deleteProduct(eq(productId), eq(userRole), eq(memberId));
        // when
        mvc.perform(delete("/api/v1/products/" + productId)).andExpect(status().isNoContent());
        // then
    }

    @Test
    @DisplayName("채팅 요청")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void given_productIdAndLoginUser_when_notSeller_then_returnChatRoomAfterGetOrCreateChatRoom() throws Exception {
        // given
        Long productId = 1L;
        Long buyerId = 123L;
        Long sellerId = 1L;
        String buyerNickname = "testUser";
        Product product = Fixture.createProduct();
        ChatRoom chatRoom = Fixture.createChatRoom();
        given(productService.getProduct(eq(productId))).willReturn(product);
        given(chatRoomService.getOrCreateChatRoom(eq(sellerId), eq(buyerId), eq(productId)))
                .willReturn(chatRoom);
        // when
        mvc.perform(post("/api/v1/products/" + productId + "/chats"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productTitle").value(product.getTitle()))
                .andExpect(jsonPath("$.sellStatus").value("ONGOING"))
                .andExpect(
                        jsonPath("$.sellerNickname").value(product.getMember().getNickname()))
                .andExpect(jsonPath("$.buyerNickname").value(buyerNickname));
        // then
    }
}
