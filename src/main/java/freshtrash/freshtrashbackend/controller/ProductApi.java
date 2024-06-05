package freshtrash.freshtrashbackend.controller;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.dto.request.ProductRequest;
import freshtrash.freshtrashbackend.dto.response.ChatRoomResponse;
import freshtrash.freshtrashbackend.dto.response.ProductResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.entity.Product;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.FileService;
import freshtrash.freshtrashbackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductApi {
    private final ProductService productService;
    private final FileService fileService;
    private final ChatRoomService chatRoomService;

    /**
     * 폐기물 단일 조회
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long productId) {
        productService.updateViewCount(productId);
        ProductResponse productResponse = ProductResponse.fromEntity(productService.getProduct(productId));
        return ResponseEntity.ok(productResponse);
    }

    /**
     * 폐기물 목록 조회
     * @param district 읍면동
     * @param predicate 제목 검색 (e.g. ?title={검색 키워드})
     */
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(required = false) String district,
            @QuerydslPredicate(root = Product.class) Predicate predicate,
            @PageableDefault(size = 6, sort = "createdAt", direction = DESC) Pageable pageable) {
        Page<ProductResponse> products = productService.getProducts(district, predicate, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * 폐기물 등록
     */
    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(
            @RequestPart MultipartFile imgFile,
            @RequestPart @Valid ProductRequest productRequest,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        ProductResponse productResponse = productService.addProduct(imgFile, productRequest, memberPrincipal);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @RequestPart MultipartFile imgFile,
            @RequestPart @Valid ProductRequest productRequest,
            @PathVariable Long productId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        String savedFileName = productService.findFileNameOfProduct(productId).fileName();
        ProductResponse productResponse =
                productService.updateProduct(productId, imgFile, productRequest, memberPrincipal);
        fileService.deleteFileIfExists(savedFileName);

        return ResponseEntity.ok(productResponse);
    }

    /**
     * 폐기물 삭제
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long productId, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        String savedFileName = productService.findFileNameOfProduct(productId).fileName();
        productService.deleteProduct(productId, memberPrincipal.getUserRole(), memberPrincipal.id());
        fileService.deleteFileIfExists(savedFileName);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    /**
     * 채팅 요청
     */
    @PostMapping("/{productId}/chats")
    public ResponseEntity<ChatRoomResponse> handleChatRoomRequest(
            @PathVariable Long productId, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        Product product = productService.getProduct(productId);
        Member seller = product.getMember();

        ChatRoom chatRoom = chatRoomService.getOrCreateChatRoom(seller.getId(), memberPrincipal.id(), productId);

        ChatRoomResponse response = ChatRoomResponse.fromEntity(
                chatRoom, product.getTitle(), seller.getNickname(), memberPrincipal.nickname());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
