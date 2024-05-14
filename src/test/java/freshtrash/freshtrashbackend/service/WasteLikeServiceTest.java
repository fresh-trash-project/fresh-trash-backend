package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.dto.response.WasteResponse;
import freshtrash.freshtrashbackend.repository.WasteLikeRepository;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WasteLikeServiceTest {

    @InjectMocks
    private WasteLikeService wasteLikeService;

    @Mock
    private WasteLikeRepository wasteLikeRepository;

    @Test
    @DisplayName("관심 Waste 목록 조회")
    void given_memberIdAndPageable_when_getWasteLikes_then_convertToWasteResponse() {
        // given
        Long memberId = 1L;
        int expectedSize = 1;
        Pageable pageable = PageRequest.of(0, 10);
        given(wasteLikeRepository.findAllByMember_Id(eq(memberId), eq(pageable)))
                .willReturn(new PageImpl<>(List.of(Fixture.createWasteLike())));
        // when
        Page<WasteResponse> wastes = wasteLikeService.getLikedWastes(memberId, pageable);
        // then
        assertThat(wastes.getSize()).isEqualTo(expectedSize);
    }
}