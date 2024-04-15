package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.dto.response.AlarmResponse;
import freshtrash.freshtrashbackend.repository.AlarmRepository;
import freshtrash.freshtrashbackend.repository.EmitterRepository;
import org.assertj.core.api.Assertions;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {
    @InjectMocks
    private AlarmService alarmService;

    @Mock
    private AlarmRepository alarmRepository;

    @Mock
    private EmitterRepository emitterRepository;

    @DisplayName("전체 알람 조회")
    @Test
    void getAlarms() {
        // given
        Long memberId = 1L;
        int expectedSize = 1;
        Pageable pageable = PageRequest.of(0, 10);
        given(alarmRepository.findAllByMember_IdAndReadAtIsNull(anyLong(), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(Fixture.createAlarm())));
        // when
        Page<AlarmResponse> alarms = alarmService.getAlarms(memberId, pageable);
        // then
        Assertions.assertThat(alarms.getSize()).isEqualTo(expectedSize);
    }
}
