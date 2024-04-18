package freshtrash.freshtrashbackend.service;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.dto.response.WasteResponse;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.QWaste;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.repository.WasteRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
class WasteServiceTest {
    @InjectMocks
    private WasteService wasteService;

    @Mock
    private WasteRepository wasteRepository;

    @Mock
    private FileService fileService;

    @DisplayName("Waste 단일 조회")
    @Test
    void given_wasteId_when_getWaste_then_wasteIsNotNull() {
        // given
        Long wasteId = 1L;
        given(wasteRepository.findById(anyLong())).willReturn(Optional.of(Fixture.createWaste()));
        // when
        Waste waste = wasteService.getWaste(wasteId);
        // then
        assertThat(waste).isNotNull();
    }

    @DisplayName("Waste 목록 조회")
    @Test
    void given_predicateAndPageable_when_getWastes_then_wastesSizeIsEqualToExpectedSize() {
        // given
        int expectedSize = 1;
        Predicate predicate = QWaste.waste.title.equalsIgnoreCase("title");
        Pageable pageable = PageRequest.of(0, 6);
        given(wasteRepository.findAll(anyString(), any(Predicate.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(Fixture.createWaste())));
        // when
        Page<WasteResponse> wastes = wasteService.getWastes("", predicate, pageable);
        // then
        assertThat(wastes.getSize()).isEqualTo(expectedSize);
    }

    @DisplayName("Waste 추가")
    @Test
    void given_imgFileAndWasteRequest_when_addWaste_then_wasteRequestValuesEqualsToSavedWasteValues() {
        // given
        WasteRequest wasteRequest = FixtureDto.createWasteRequest();
        MemberPrincipal memberPrincipal = FixtureDto.createMemberPrincipal();
        Waste waste = Waste.fromRequest(wasteRequest, "test.png", memberPrincipal.id());
        given(wasteRepository.save(any(Waste.class))).willReturn(waste);
        willDoNothing().given(fileService).uploadFile(any(MultipartFile.class), anyString());
        // when
        WasteResponse wasteResponse = wasteService.addWaste(Fixture.createMultipartFile("image"), wasteRequest, memberPrincipal);
        // then
        assertThat(wasteResponse.title()).isEqualTo(wasteRequest.title());
        assertThat(wasteResponse.content()).isEqualTo(wasteRequest.content());
        assertThat(wasteResponse.wasteCategory()).isEqualTo(wasteRequest.wasteCategory());
        assertThat(wasteResponse.wasteStatus()).isEqualTo(wasteRequest.wasteStatus());
        assertThat(wasteResponse.sellStatus()).isEqualTo(wasteRequest.sellStatus());
        assertThat(wasteResponse.wastePrice()).isEqualTo(wasteRequest.wastePrice());
        assertThat(wasteResponse.address()).isEqualTo(wasteRequest.address());
    }

    @DisplayName("Waste 수정_파일이 유효한 경우_저장된 파일명은 이전에 저장한 파일명과 상이")
    @Test
    void given_imgFileAndWasteRequest_when_updateWaste_then_wasteRequestValuesEqualsToUpdatedWasteValues() {
        // given
        Long wasteId = 1L;
        MockMultipartFile multipartFile = Fixture.createMultipartFile("test content");
        WasteRequest wasteRequest = FixtureDto.createWasteRequest();
        String savedFileName = "saved.png";
        MemberPrincipal memberPrincipal = FixtureDto.createMemberPrincipal();
        willDoNothing().given(fileService).uploadFile(any(MultipartFile.class), anyString());
        willDoNothing().given(wasteRepository).flush();
        willDoNothing().given(fileService).deleteFileIfExists(anyString());
        // when
        WasteResponse wasteResponse = wasteService.updateWaste(wasteId, multipartFile, wasteRequest, savedFileName, memberPrincipal);
        // then
        assertThat(wasteResponse.title()).isEqualTo(wasteRequest.title());
        assertThat(wasteResponse.content()).isEqualTo(wasteRequest.content());
        assertThat(wasteResponse.wasteCategory()).isEqualTo(wasteRequest.wasteCategory());
        assertThat(wasteResponse.wasteStatus()).isEqualTo(wasteRequest.wasteStatus());
        assertThat(wasteResponse.sellStatus()).isEqualTo(wasteRequest.sellStatus());
        assertThat(wasteResponse.wastePrice()).isEqualTo(wasteRequest.wastePrice());
        assertThat(wasteResponse.address()).isEqualTo(wasteRequest.address());
        assertThat(wasteResponse.fileName()).isNotEqualTo(savedFileName);
    }

    @DisplayName("Waste 삭제")
    @Test
    void given_wasteId_when_then_deleteWasteAndFile() {
        // given
        Long wasteId = 1L;
        String savedFileName = "saved.png";
        willDoNothing().given(wasteRepository).deleteById(anyLong());
        willDoNothing().given(fileService).deleteFileIfExists(anyString());
        // when
        wasteService.deleteWaste(wasteId, savedFileName);
        // then
    }
}