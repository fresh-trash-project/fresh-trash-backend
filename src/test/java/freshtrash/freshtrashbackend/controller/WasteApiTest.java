package freshtrash.freshtrashbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.dto.WasteDto;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.service.impl.WasteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WasteApi.class)
class WasteApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private WasteService wasteService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("폐기물 등록")
    @Test
    void addWaste() throws Exception {
        // given
        MockMultipartFile imgFile = Fixture.createMultipartFile("test_image");
        WasteRequest wasteRequest = FixtureDto.createWasteRequest();
        WasteDto wasteDto = FixtureDto.createWasteDto();
        given(wasteService.addWaste(any(MultipartFile.class), any(WasteRequest.class)))
                .willReturn(wasteDto);
        // when
        mvc.perform(multipart(HttpMethod.POST, "/api/v1/wastes")
                        .file("imgFile", imgFile.getBytes())
                        .file(new MockMultipartFile(
                                "wasteRequest", "", "application/json", objectMapper.writeValueAsBytes(wasteRequest)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"));
        // then
    }
}
