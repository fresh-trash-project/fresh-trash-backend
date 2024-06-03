package freshtrash.freshtrashbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.dto.response.WasteResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.entity.constants.WasteCategory;
import freshtrash.freshtrashbackend.entity.constants.WasteStatus;
import freshtrash.freshtrashbackend.repository.projections.FileNameSummary;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.LocalFileService;
import freshtrash.freshtrashbackend.service.WasteService;
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

@WebMvcTest(WasteApi.class)
@Import(TestSecurityConfig.class)
class WasteApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private WasteService wasteService;

    @MockBean
    private LocalFileService localFileService;

    @MockBean
    private ChatRoomService chatRoomService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("폐기물 단일 조회")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    void given_wasteId_when_getWaste_then_returnWasteData() throws Exception {
        // given
        Long wasteId = 1L;
        Waste waste = Fixture.createWaste();
        given(wasteService.getWaste(eq(wasteId))).willReturn(waste);
        // when
        mvc.perform(get("/api/v1/wastes/" + wasteId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(waste.getTitle()))
                .andExpect(jsonPath("$.content").value(waste.getContent()))
                .andExpect(jsonPath("$.wastePrice").value(waste.getWastePrice()))
                .andExpect(jsonPath("$.fileName").value(waste.getFileName()))
                .andExpect(jsonPath("$.likeCount").value(waste.getLikeCount()))
                .andExpect(jsonPath("$.viewCount").value(waste.getViewCount()))
                .andExpect(jsonPath("$.wasteCategory")
                        .value(waste.getWasteCategory().name()))
                .andExpect(
                        jsonPath("$.wasteStatus").value(waste.getWasteStatus().name()))
                .andExpect(jsonPath("$.sellStatus").value(waste.getSellStatus().name()));
        // then
    }

    @Test
    @DisplayName("페기물 목록 조회")
    void given_predicateAndPageable_when_getWastes_then_returnPagingWasteData() throws Exception {
        // given
        given(wasteService.getWastes(eq(null), any(Predicate.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(WasteResponse.fromEntity(Fixture.createWaste()))));
        // when
        mvc.perform(get("/api/v1/wastes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(1));
        // then
    }

    @Test
    @DisplayName("폐기물 등록")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    void given_imgFileAndWasteRequest_when_requestAddWaste_then_requestValuesEqualsToReturnedWasteValues()
            throws Exception {
        Long memberId = 1L;
        // given
        MockMultipartFile imgFile = Fixture.createMultipartFileOfImage("test_image");
        WasteRequest wasteRequest = FixtureDto.createWasteRequest();
        Waste waste = Waste.fromRequest(wasteRequest, imgFile.getOriginalFilename(), memberId);
        ReflectionTestUtils.setField(waste, "member", Fixture.createMember());
        WasteResponse wasteResponse = WasteResponse.fromEntity(waste);
        given(wasteService.addWaste(any(MultipartFile.class), any(WasteRequest.class), any(MemberPrincipal.class)))
                .willReturn(wasteResponse);
        // when
        mvc.perform(multipart(HttpMethod.POST, "/api/v1/wastes")
                        .file("imgFile", imgFile.getBytes())
                        .file(new MockMultipartFile(
                                "wasteRequest", "", "application/json", objectMapper.writeValueAsBytes(wasteRequest)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(wasteRequest.title()))
                .andExpect(jsonPath("$.content").value(wasteRequest.content()))
                .andExpect(jsonPath("$.wastePrice").value(wasteRequest.wastePrice()))
                .andExpect(jsonPath("$.fileName").value(imgFile.getOriginalFilename()))
                .andExpect(jsonPath("$.wasteCategory")
                        .value(wasteRequest.wasteCategory().name()))
                .andExpect(jsonPath("$.wasteStatus")
                        .value(wasteRequest.wasteStatus().name()))
                .andExpect(
                        jsonPath("$.sellStatus").value(wasteRequest.sellStatus().name()));
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
    void given_missingOneInImgFileAndWasteRequest_when_requestAddWaste_then_failed(
            String title,
            String content,
            WasteCategory wasteCategory,
            WasteStatus wasteStatus,
            SellStatus sellStatus,
            Integer wastePrice,
            String zipcode,
            String state,
            String city,
            String district,
            String detail)
            throws Exception {
        // given
        MockMultipartFile imgFile = Fixture.createMultipartFileOfImage("test_image");
        WasteRequest wasteRequest = FixtureDto.createWasteRequest(
                title,
                content,
                wasteCategory,
                wasteStatus,
                sellStatus,
                wastePrice,
                Address.builder()
                        .zipcode(zipcode)
                        .state(state)
                        .city(city)
                        .district(district)
                        .detail(detail)
                        .build());
        // when
        mvc.perform(multipart(HttpMethod.POST, "/api/v1/wastes")
                        .file("imgFile", imgFile.getBytes())
                        .file(new MockMultipartFile(
                                "wasteRequest", "", "application/json", objectMapper.writeValueAsBytes(wasteRequest)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        // then
    }

    @Test
    @DisplayName("폐기물 수정")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    void given_imgFileAndWasteRequest_when_requestUpdateWaste_then_requestValuesEqualsToReturnedWasteValues()
            throws Exception {
        // given
        Long wasteId = 1L, memberId = 123L;
        String fileName = "test.png";
        MockMultipartFile imgFile = Fixture.createMultipartFileOfImage("test_image");
        WasteRequest wasteRequest = FixtureDto.createWasteRequest();
        Waste waste = Waste.fromRequest(wasteRequest, imgFile.getOriginalFilename(), memberId);
        ReflectionTestUtils.setField(waste, "member", Fixture.createMember());
        WasteResponse wasteResponse = WasteResponse.fromEntity(waste);
        given(wasteService.isWriterOfArticle(eq(wasteId), eq(memberId))).willReturn(true);
        given(wasteService.findFileNameOfWaste(eq(wasteId))).willReturn(new FileNameSummary(fileName));
        given(wasteService.updateWaste(
                        eq(wasteId), any(MultipartFile.class), any(WasteRequest.class), any(MemberPrincipal.class)))
                .willReturn(wasteResponse);
        willDoNothing().given(localFileService).deleteFileIfExists(eq(fileName));
        // when
        mvc.perform(multipart(HttpMethod.PUT, "/api/v1/wastes/" + wasteId)
                        .file("imgFile", imgFile.getBytes())
                        .file(new MockMultipartFile(
                                "wasteRequest", "", "application/json", objectMapper.writeValueAsBytes(wasteRequest)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(wasteRequest.title()))
                .andExpect(jsonPath("$.content").value(wasteRequest.content()))
                .andExpect(jsonPath("$.wastePrice").value(wasteRequest.wastePrice()))
                .andExpect(jsonPath("$.fileName").value(imgFile.getOriginalFilename()))
                .andExpect(jsonPath("$.wasteCategory")
                        .value(wasteRequest.wasteCategory().name()))
                .andExpect(jsonPath("$.wasteStatus")
                        .value(wasteRequest.wasteStatus().name()))
                .andExpect(
                        jsonPath("$.sellStatus").value(wasteRequest.sellStatus().name()));
        // then
    }

    @Test
    @DisplayName("페기물 삭제")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    void given_wasteIdAndWriter_when_then_deleteWasteAndFile() throws Exception {
        // given
        Long wasteId = 1L;
        Long memberId = 123L;
        String fileName = "test.png";
        given(wasteService.isWriterOfArticle(eq(wasteId), eq(memberId))).willReturn(true);
        given(wasteService.findFileNameOfWaste(eq(wasteId))).willReturn(new FileNameSummary(fileName));
        willDoNothing().given(localFileService).deleteFileIfExists(eq(fileName));
        willDoNothing().given(wasteService).deleteWaste(eq(wasteId));
        // when
        mvc.perform(delete("/api/v1/wastes/" + wasteId)).andExpect(status().isNoContent());
        // then
    }

    @Test
    @DisplayName("채팅 요청")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void given_wasteIdAndLoginUser_when_notSeller_then_returnChatRoomAfterGetOrCreateChatRoom() throws Exception {
        // given
        Long wasteId = 1L;
        Long buyerId = 123L;
        Long sellerId = 1L;
        String buyerNickname = "testUser";
        Waste waste = Fixture.createWaste();
        ChatRoom chatRoom = Fixture.createChatRoom();
        given(wasteService.getWaste(eq(wasteId))).willReturn(waste);
        given(chatRoomService.getOrCreateChatRoom(eq(sellerId), eq(buyerId), eq(wasteId)))
                .willReturn(chatRoom);
        // when
        mvc.perform(post("/api/v1/wastes/" + wasteId + "/chats"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.wasteTitle").value(waste.getTitle()))
                .andExpect(jsonPath("$.sellStatus").value("ONGOING"))
                .andExpect(jsonPath("$.sellerNickname").value(waste.getMember().getNickname()))
                .andExpect(jsonPath("$.buyerNickname").value(buyerNickname));
        // then
    }
}
