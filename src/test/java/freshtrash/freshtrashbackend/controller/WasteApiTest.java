package freshtrash.freshtrashbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.dto.WasteDto;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Address;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.entity.constants.WasteCategory;
import freshtrash.freshtrashbackend.entity.constants.WasteStatus;
import freshtrash.freshtrashbackend.service.WasteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.context.support.TestExecutionEvent.TEST_EXECUTION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfig.class)
@WebMvcTest(WasteApi.class)
class WasteApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private WasteService wasteService;

    @Autowired
    private ObjectMapper objectMapper;

    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    @DisplayName("폐기물 등록")
    @Test
    void addWaste() throws Exception {
        // given
        MockMultipartFile imgFile = Fixture.createMultipartFile("test_image");
        WasteRequest wasteRequest = FixtureDto.createWasteRequest();
        WasteDto wasteDto = FixtureDto.createWasteDto();
        given(wasteService.addWaste(any(MultipartFile.class), any(WasteRequest.class), any(MemberPrincipal.class)))
                .willReturn(wasteDto);
        // when
        mvc.perform(multipart(HttpMethod.POST, "/api/v1/wastes")
                        .file("imgFile", imgFile.getBytes())
                        .file(new MockMultipartFile(
                                "wasteRequest", "", "application/json", objectMapper.writeValueAsBytes(wasteRequest)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.wastePrice").value(1000))
                .andExpect(jsonPath("$.likeCount").value(2))
                .andExpect(jsonPath("$.viewCount").value(3))
                .andExpect(jsonPath("$.fileName").value("test.png"))
                .andExpect(jsonPath("$.wasteCategory").value("BEAUTY"))
                .andExpect(jsonPath("$.wasteStatus").value("BEST"))
                .andExpect(jsonPath("$.sellStatus").value("CLOSE"));
        // then
    }

    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    @DisplayName("어느 하나라도 입력되지 않았을 경우 폐기물 등록 실패")
    @ParameterizedTest
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
    void addWaste_Failed(
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
        MockMultipartFile imgFile = Fixture.createMultipartFile("test_image");
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

    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    @DisplayName("폐기물 수정")
    @Test
    void updateWaste() throws Exception {
        // given
        Long wasteId = 1L, memberId = 1L;
        MockMultipartFile imgFile = Fixture.createMultipartFile("test_image");
        WasteRequest wasteRequest = FixtureDto.createWasteRequest();
        WasteDto wasteDto = FixtureDto.createWasteDto();
        given(wasteService.isWriterOfArticle(wasteId, memberId)).willReturn(true);
        given(wasteService.findFileNameOfWaste(anyLong())).willReturn("test.png");
        given(wasteService.updateWaste(
                        any(MultipartFile.class), any(WasteRequest.class), anyString(), any(MemberPrincipal.class)))
                .willReturn(wasteDto);
        // when
        mvc.perform(multipart(HttpMethod.PUT, "/api/v1/wastes/" + wasteId)
                        .file("imgFile", imgFile.getBytes())
                        .file(new MockMultipartFile(
                                "wasteRequest", "", "application/json", objectMapper.writeValueAsBytes(wasteRequest)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.wastePrice").value(1000))
                .andExpect(jsonPath("$.likeCount").value(2))
                .andExpect(jsonPath("$.viewCount").value(3))
                .andExpect(jsonPath("$.fileName").value("test.png"))
                .andExpect(jsonPath("$.wasteCategory").value("BEAUTY"))
                .andExpect(jsonPath("$.wasteStatus").value("BEST"))
                .andExpect(jsonPath("$.sellStatus").value("CLOSE"));
        // then
    }
}
