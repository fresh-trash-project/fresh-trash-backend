package freshtrash.freshtrashbackend.Fixture;

import freshtrash.freshtrashbackend.entity.*;
import freshtrash.freshtrashbackend.entity.constants.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Set;

public class Fixture {
    public static Product createProduct() {
        Product product = Product.builder()
                .title("title")
                .content("content")
                .productPrice(1000)
                .fileName("test.png")
                .productCategory(ProductCategory.BEAUTY)
                .productStatus(ProductStatus.BEST)
                .sellStatus(SellStatus.CLOSE)
                .address(createAddress())
                .memberId(1L)
                .build();
        ReflectionTestUtils.setField(product, "member", Fixture.createMember());
        ReflectionTestUtils.setField(product, "likeCount", 3);
        ReflectionTestUtils.setField(product, "viewCount", 2);
        return product;
    }

    public static MockMultipartFile createMultipartFileOfImage(String content) {
        String fileName = "test.png";
        return new MockMultipartFile("modelFile", fileName, "text/plain", content.getBytes(StandardCharsets.UTF_8));
    }

    public static MockMultipartFile createMultipartFileOfJson(String name, String content) {
        return new MockMultipartFile(name, "", "application/json", content.getBytes(StandardCharsets.UTF_8));
    }

    public static Address createAddress() {
        return Address.builder()
                .zipcode("12345")
                .state("state")
                .city("city")
                .district("district")
                .detail("detail")
                .build();
    }

    public static Member createMember(
            Long id, String email, String password, String nickname, LoginType loginType, AccountStatus accountStatus) {
        Member member = Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .loginType(loginType)
                .userRole(UserRole.USER)
                .accountStatus(accountStatus)
                .build();

        ReflectionTestUtils.setField(member, "id", id);
        ReflectionTestUtils.setField(member, "fileName", "test.png");
        ReflectionTestUtils.setField(member, "address", Fixture.createAddress());

        return member;
    }

    public static Member createMember() {
        return createMember(1L, "test@gmail.com", "pw", "test", LoginType.EMAIL, AccountStatus.ACTIVE);
    }

    public static Member createLoginMember() {
        return createMember(
                123L, "testUser@gmail.com", "pw", "testUser", LoginType.EMAIL, AccountStatus.ACTIVE);
    }

    public static ChatRoom createChatRoom(
            Long productId, Long sellerId, Long buyerId, boolean openOrClose, SellStatus sellStatus) {
        ChatRoom chatRoom = ChatRoom.builder()
                .buyerId(buyerId)
                .sellerId(sellerId)
                .productId(productId)
                .openOrClose(openOrClose)
                .sellStatus(sellStatus)
                .build();
        ReflectionTestUtils.setField(chatRoom, "buyer", Fixture.createMember());
        return chatRoom;
    }

    public static Alarm createAlarm() {
        return Alarm.builder()
                .memberId(1L)
                .alarmArgs(AlarmArgs.of(3L, 2L))
                .alarmType(AlarmType.TRANSACTION)
                .build();
    }

    public static ChatRoom createChatRoom() {
        Long productId = 1L;
        Long buyerId = 2L;
        Long sellerId = 3L;
        ChatRoom chatRoom = ChatRoom.builder()
                .sellStatus(SellStatus.ONGOING)
                .openOrClose(true)
                .productId(productId)
                .buyerId(buyerId)
                .sellerId(sellerId)
                .build();
        ReflectionTestUtils.setField(chatRoom, "product", createProduct());
        ReflectionTestUtils.setField(
                chatRoom,
                "seller",
                createMember(sellerId, "seller@gmail.com", "pw", "seller", LoginType.EMAIL, AccountStatus.ACTIVE));
        ReflectionTestUtils.setField(
                chatRoom,
                "buyer",
                createMember(buyerId, "buyer@gmail.com", "pw", "buyer", LoginType.EMAIL, AccountStatus.ACTIVE));
        ReflectionTestUtils.setField(chatRoom, "chatMessages", Set.of(createChatMessage()));
        return chatRoom;
    }

    public static ChatMessage createChatMessage() {
        return ChatMessage.of(1L, createMember(), "message");
    }

    public static ProductDealLog createProductDealLog(Long productId, Long sellerId, Long buyerId) {
        return ProductDealLog.builder()
                .productId(productId)
                .sellerId(sellerId)
                .buyerId(buyerId)
                .build();
    }

    public static ProductLike createProductLike() {
        ProductLike productLike = ProductLike.of(1L, 2L);
        ReflectionTestUtils.setField(productLike, "product", createProduct());
        return productLike;
    }

    public static ProductDealLog createProductDealLog() {
        ProductDealLog productDealLog =
                ProductDealLog.builder().buyerId(1L).sellerId(2L).productId(3L).build();
        ReflectionTestUtils.setField(productDealLog, "product", createProduct());
        return productDealLog;
    }
}
