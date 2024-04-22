package freshtrash.freshtrashbackend.Fixture;

import freshtrash.freshtrashbackend.entity.*;
import freshtrash.freshtrashbackend.entity.constants.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Set;

public class Fixture {
    public static Waste createWaste() {
        Waste waste = Waste.builder()
                .title("title")
                .content("content")
                .wastePrice(1000)
                .fileName("test.png")
                .wasteCategory(WasteCategory.BEAUTY)
                .wasteStatus(WasteStatus.BEST)
                .sellStatus(SellStatus.CLOSE)
                .address(createAddress())
                .memberId(1L)
                .build();
        ReflectionTestUtils.setField(waste, "member", Fixture.createMember());
        ReflectionTestUtils.setField(waste, "likeCount", 3);
        ReflectionTestUtils.setField(waste, "viewCount", 2);
        return waste;
    }

    public static MockMultipartFile createMultipartFile(String content) {
        String fileName = "test.png";
        return new MockMultipartFile("modelFile", fileName, "text/plain", content.getBytes(StandardCharsets.UTF_8));
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
            String email,
            String password,
            String nickname,
            LoginType loginType,
            UserRole userRole,
            AccountStatus accountStatus) {
        Member member = Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .loginType(loginType)
                .userRole(userRole)
                .accountStatus(accountStatus)
                .build();

        ReflectionTestUtils.setField(member, "id", 1L);
        ReflectionTestUtils.setField(member, "fileName", "test.png");
        ReflectionTestUtils.setField(member, "address", Fixture.createAddress());

        return member;
    }

    public static Member createMember() {
        return createMember("test@gmail.com", "pw", "test", LoginType.EMAIL, UserRole.USER, AccountStatus.ACTIVE);
    }

    public static ChatRoom createChatRoom(
            Long wasteId, Long sellerId, Long buyerId, boolean openOrClose, SellStatus sellStatus) {
        return ChatRoom.builder()
                .buyerId(buyerId)
                .sellerId(sellerId)
                .wasteId(wasteId)
                .openOrClose(openOrClose)
                .sellStatus(sellStatus)
                .build();
    }

    public static Alarm createAlarm() {
        return Alarm.builder()
                .memberId(1L)
                .alarmArgs(AlarmArgs.of(3L, 2L))
                .alarmType(AlarmType.TRANSACTION)
                .build();
    }

    public static ChatRoom createChatRoom() {
        ChatRoom chatRoom = ChatRoom.builder()
                .sellStatus(SellStatus.ONGOING)
                .openOrClose(true)
                .wasteId(1L)
                .buyerId(2L)
                .sellerId(3L)
                .build();
        ReflectionTestUtils.setField(
                chatRoom,
                "seller",
                createMember("seller@gmail.com", "pw", "seller", LoginType.EMAIL, UserRole.USER, AccountStatus.ACTIVE));
        ReflectionTestUtils.setField(
                chatRoom,
                "buyer",
                createMember("buyer@gmail.com", "pw", "buyer", LoginType.EMAIL, UserRole.USER, AccountStatus.ACTIVE));
        ReflectionTestUtils.setField(chatRoom, "chatMessages", Set.of(createChatMessage()));
        return chatRoom;
    }

    public static ChatMessage createChatMessage() {
        return ChatMessage.of(1L, 2L, "message");
    }

    public static TransactionLog createTransactionLog(Long wasteId, Long sellerId, Long buyerId) {
        return TransactionLog.builder()
                .wasteId(wasteId)
                .sellerId(sellerId)
                .buyerId(buyerId)
                .build();
    }

    public static WasteLike createWasteLike() {
        WasteLike wasteLike = WasteLike.of(1L, 2L);
        ReflectionTestUtils.setField(wasteLike, "waste", createWaste());
        return wasteLike;
    }

    public static TransactionLog createTransactionLog() {
        TransactionLog transactionLog =
                TransactionLog.builder().buyerId(1L).sellerId(2L).wasteId(3L).build();
        ReflectionTestUtils.setField(transactionLog, "waste", createWaste());
        return transactionLog;
    }
}
