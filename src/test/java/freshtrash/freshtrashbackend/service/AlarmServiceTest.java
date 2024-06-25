package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.dto.request.BaseAlarmPayload;
import freshtrash.freshtrashbackend.dto.response.AlarmResponse;
import freshtrash.freshtrashbackend.entity.Alarm;
import freshtrash.freshtrashbackend.exception.AlarmException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.AlarmRepository;
import freshtrash.freshtrashbackend.repository.EmitterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {
    @InjectMocks
    private AlarmService alarmService;

    @Mock
    private AlarmRepository alarmRepository;

    @Mock
    private EmitterRepository emitterRepository;

    @Test
    @DisplayName("전체 알람 조회")
    void given_memberIdAndPageable_when_then_getPagingAlarms() {
        // given
        Long memberId = 1L;
        int expectedSize = 1;
        Pageable pageable = PageRequest.of(0, 10);
        given(alarmRepository.findAllByMember_Id(eq(memberId), eq(pageable)))
                .willReturn(new PageImpl<>(List.of(Fixture.createAlarm())));
        // when
        Page<AlarmResponse> alarms = alarmService.getAlarms(memberId, pageable);
        // then
        assertThat(alarms.getSize()).isEqualTo(expectedSize);
    }

    @Test
    @DisplayName("baseAlarmPayload를 입력받아 알람 저장")
    void given_AlarmPayload_when_then_saveAlarm() {
        // given
        BaseAlarmPayload alarmPayload = FixtureDto.createProductAlarmPayload();
        Alarm alarm = Alarm.fromAlarmPayload(alarmPayload);
        given(alarmRepository.save(alarm)).willReturn(alarm);
        // when
        Alarm savedAlarm = alarmService.saveAlarm(alarmPayload);
        // then
        assertThat(savedAlarm).isNotNull();
    }

    @Test
    @DisplayName("memberId를 입력받아 SseEmitter를 생성하여 반환한다.")
    void given_memberId_when_saveEmitter_then_returnEmitter() {
        // given
        Long memberId = 1L;
        SseEmitter sseEmitter = new SseEmitter(30L);
        given(emitterRepository.save(eq(memberId), any(SseEmitter.class))).willReturn(sseEmitter);
        // when
        SseEmitter savedSseEmitter = alarmService.connectAlarm(memberId);
        // then
        assertThat(savedSseEmitter).isNotNull();
    }

    @Test
    @DisplayName("alarmId와 memberId를 받고 해당 알림 대상자가 맞다면 알람을 읽음 처리한다.")
    void given_alarmId_when_readAlarm_then_updateReadAtToNow() {
        // given
        Long alarmId = 1L, memberId = 123L;
        given(alarmRepository.existsByIdAndMember_Id(eq(alarmId), eq(memberId))).willReturn(true);
        willDoNothing().given(alarmRepository).updateReadAtById(eq(alarmId));
        // when
        alarmService.readAlarm(alarmId, memberId);
        // then
        then(alarmRepository).should(times(1)).updateReadAtById(anyLong());
    }

    @Test
    @DisplayName("alarmId와 memberId를 받고 해당 알림 대상자가 아니라면 예외를 발생시킨다.")
    void given_alarmId_when_notOwner_then_throwException() {
        // given
        Long alarmId = 1L, memberId = 123L;
        given(alarmRepository.existsByIdAndMember_Id(eq(alarmId), eq(memberId))).willReturn(false);
        // when
        assertThatThrownBy(() -> alarmService.readAlarm(alarmId, memberId))
                .isInstanceOf(AlarmException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ALARM);
        // then
    }

    @Test
    @DisplayName("매달 0시에 1개월이 지난 알람을 모두 삭제한다.")
    void should_deleteAlarms_when_passes1Month() {
        // given
        LocalDateTime now = LocalDateTime.now();
        try (MockedStatic<LocalDateTime> dateTimeMockedStatic = Mockito.mockStatic(LocalDateTime.class)) {
            dateTimeMockedStatic.when(LocalDateTime::now).thenReturn(now);
            willDoNothing()
                    .given(alarmRepository)
                    .deleteAllInBatchByReadAtNotNullAndCreatedAtBefore(now.minusMonths(1));
            // when
            assertThatCode(() -> alarmService.deleteAlarms()).doesNotThrowAnyException();
        }
        // then
    }

    @Test
    @DisplayName("memberId와 AlarmResponse를 입력받고 SSE 전송을 수행한다.")
    void given_memberIdAndAlarmResponse_when_ifPresentEmitter_then_sendAlarm() {
        // given
        Long memberId = 1L;
        AlarmResponse alarmResponse = FixtureDto.createAlarmResponse();
        SseEmitter sseEmitter = new SseEmitter(30L);
        given(emitterRepository.findByMemberId(memberId)).willReturn(Optional.of(sseEmitter));
        // when
        assertThatCode(() -> alarmService.receive(memberId, alarmResponse)).doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("memberId와 AlarmResponse를 입력받고 조회한 Emitter에 문제가 발생할 경우 해당 Emititer를 삭제하고 예외를 발생시킨다.")
    void given_memberIdAndAlarmResponse_when_badEmitter_then_deleteEmitterAndThrowException() {
        // given
        Long memberId = 1L;
        AlarmResponse alarmResponse = FixtureDto.createAlarmResponse();
        SseEmitter sseEmitter = new SseEmitter(0L);
        sseEmitter.complete();
        given(emitterRepository.findByMemberId(memberId)).willReturn(Optional.of(sseEmitter));
        willDoNothing().given(emitterRepository).deleteByMemberId(memberId);
        // when
        assertThatThrownBy(() -> alarmService.receive(memberId, alarmResponse))
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALARM_CONNECT_ERROR);
        // then
    }

    @Test
    @DisplayName("memberId와 AlarmResponse를 입력받고 Emitter가 존재하지 않을 경우 에러 로그를 출력한다.")
    void given_memberIdAndAlarmResponse_when_notFoundEmitter_then_printErrorLog() {
        // given
        Long memberId = 1L;
        AlarmResponse alarmResponse = FixtureDto.createAlarmResponse();
        given(emitterRepository.findByMemberId(memberId)).willReturn(Optional.empty());
        // when
        assertThatCode(() -> alarmService.receive(memberId, alarmResponse)).doesNotThrowAnyException();
        // then
    }
}
