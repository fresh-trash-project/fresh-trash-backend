package freshtrash.freshtrashbackend.Fixture;

import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.entity.constants.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;

public class Fixture {
    public static Waste createWaste() {
        return Waste.builder()
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
        Member member = Member.of(email, password, nickname, loginType, userRole, accountStatus);
        ReflectionTestUtils.setField(member, "id", 1L);
        ReflectionTestUtils.setField(member, "fileName", "test.png");
        ReflectionTestUtils.setField(member, "address", Fixture.createAddress());
        return member;
    }
}
