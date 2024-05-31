package freshtrash.freshtrashbackend.controller;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.dto.response.WasteResponse;
import freshtrash.freshtrashbackend.entity.constants.WasteCategory;
import freshtrash.freshtrashbackend.service.WasteLikeService;
import freshtrash.freshtrashbackend.service.WasteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.context.support.TestExecutionEvent.TEST_EXECUTION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(WasteLikeApi.class)
@Import(TestSecurityConfig.class)
class WasteLikeApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private WasteLikeService wasteLikeService;

    @MockBean
    private WasteService wasteService;

    @Test
    @DisplayName("관심 폐기물 목록 조회")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    void given_loginUserAndPageable_when_getLikedWastes_then_returnPagingWasteData() throws Exception {
        // given
        WasteCategory category = WasteCategory.BEAUTY;
        Pageable pageable = PageRequest.of(0, 6, Sort.Direction.DESC, "createdAt");
        given(wasteLikeService.getLikedWastes(any(Predicate.class), eq(pageable)))
                .willReturn(new PageImpl<>(List.of(WasteResponse.fromEntity(Fixture.createWaste()))));
        // when
        mvc.perform(get("/api/v1/wastes/likes").queryParam("category", category.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(1));
        // then
    }
}