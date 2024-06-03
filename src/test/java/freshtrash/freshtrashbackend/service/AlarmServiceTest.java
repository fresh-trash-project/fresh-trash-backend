package freshtrash.freshtrashbackend.service;

import com.rabbitmq.client.Channel;
import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.dto.request.AlarmPayload;
import freshtrash.freshtrashbackend.dto.response.AlarmResponse;
import freshtrash.freshtrashbackend.entity.Alarm;
import freshtrash.freshtrashbackend.repository.AlarmRepository;
import freshtrash.freshtrashbackend.repository.EmitterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    @DisplayName("알람 읽음 처리")
    void given_alarmId_when_readAlarm_then_updateReadAtToNow() {
        // given
        Long alarmId = 1L;
        willDoNothing().given(alarmRepository).updateReadAtById(eq(alarmId));
        // when
        alarmService.readAlarm(alarmId);
        // then
        then(alarmRepository).should(times(1)).updateReadAtById(anyLong());
    }

    @Test
    @DisplayName("알람 메시지 전송")
    void given_alarmPayload_when_listenMessage_then_saveAlarmAndSendAlarm() {
        // given
        AlarmPayload alarmPayload = FixtureDto.createAlarmPayload();
        Long memberId = alarmPayload.memberId();
        Alarm alarm = Alarm.fromMessageRequest(alarmPayload);
        SseEmitter sseEmitter = new SseEmitter(TimeUnit.MINUTES.toMillis(30));
        Channel channel = mock(Channel.class);
        long deliveryTag = 3;
        given(alarmRepository.save(eq(alarm))).willReturn(alarm);
        given(emitterRepository.findByMemberId(eq(memberId))).willReturn(Optional.of(sseEmitter));
        // whenxp
        alarmService.receiveProductProductDeal(channel, deliveryTag, alarmPayload);
        ArgumentCaptor<Alarm> alarmCaptor = ArgumentCaptor.forClass(Alarm.class);
        // then
        verify(alarmRepository, times(1)).save(alarmCaptor.capture());
        verify(emitterRepository, times(1)).findByMemberId(eq(memberId));
    }
}
