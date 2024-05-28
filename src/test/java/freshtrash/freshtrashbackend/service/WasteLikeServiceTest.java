package freshtrash.freshtrashbackend.service;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.dto.response.WasteResponse;
import freshtrash.freshtrashbackend.entity.QWasteLike;
import freshtrash.freshtrashbackend.entity.constants.WasteCategory;
import freshtrash.freshtrashbackend.entity.WasteLike;
import freshtrash.freshtrashbackend.repository.WasteLikeRepository;
import freshtrash.freshtrashbackend.repository.WasteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class WasteLikeServiceTest {

    @InjectMocks
    private WasteLikeService wasteLikeService;

    @Mock
    private WasteLikeRepository wasteLikeRepository;

    @Mock
    private WasteRepository wasteRepository;

    @Test
    @DisplayName("관심 Waste 목록 조회")
    void given_memberIdAndPageable_when_getWasteLikes_then_convertToWasteResponse() {
        // given
        Long memberId = 1L;
        WasteCategory category = WasteCategory.BEAUTY;
        int expectedSize = 1;
        Predicate predicate =
                QWasteLike.wasteLike.memberId.eq(memberId).and(QWasteLike.wasteLike.waste.wasteCategory.eq(category));
        Pageable pageable = PageRequest.of(0, 6, Sort.Direction.DESC, "createdAt");
        given(wasteLikeRepository.findAll(eq(predicate), eq(pageable)))
                .willReturn(new PageImpl<>(List.of(Fixture.createWasteLike())));
        // when
        Page<WasteResponse> wastes = wasteLikeService.getLikedWastes(predicate, pageable);
        // then
        assertThat(wastes.getSize()).isEqualTo(expectedSize);
    }

    @Test
    @DisplayName("관심 폐기물 추가")
    void given_memberIdAndWasteId_when_addWasteLike_then_saveWasteLikeAndUpdateLikeCount() {
        // given
        Long memberId = 123L;
        Long wasteId = 1L;
        WasteLike wasteLike = WasteLike.of(memberId, wasteId);
        given(wasteLikeRepository.save(any(WasteLike.class))).willReturn(wasteLike);
        willDoNothing().given(wasteRepository).updateLikeCount(wasteId, 1);

        // when
        wasteLikeService.addWasteLike(memberId, wasteId);
        // then
    }

    @Test
    @DisplayName("관심 폐기물 삭제")
    void given_memberIdAndWasteId_when_deleteWasteLike_then_deleteWasteLikeAndUpdateLikeCount() {
        // given
        Long memberId = 123L;
        Long wasteId = 1L;
        given(wasteLikeRepository.existsByMemberIdAndWasteId(memberId, wasteId)).willReturn(true);
        willDoNothing().given(wasteLikeRepository).deleteByMemberIdAndWasteId(memberId, wasteId);
        willDoNothing().given(wasteRepository).updateLikeCount(wasteId, -1);

        // when
        wasteLikeService.deleteWasteLike(memberId, wasteId);
        // then
    }
}