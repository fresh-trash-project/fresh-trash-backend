package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.service.AlarmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.context.support.TestExecutionEvent.TEST_EXECUTION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfig.class)
@WebMvcTest(AlarmApi.class)
class AlarmApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AlarmService alarmService;

    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    @DisplayName("알람 목록 조회")
    @Test
    void given_loginUserAndPageable_when_then_getPagingNotis() throws Exception {
        // given
        Long memberId = 123L;
        Pageable pageable = PageRequest.of(0, 10);
        given(alarmService.getAlarms(eq(memberId), eq(pageable)))
                .willReturn(new PageImpl<>(List.of(Fixture.createAlarm().toResponse())));
        // when
        mvc.perform(get("/api/v1/notis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(1));
        // then
    }

    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    @DisplayName("알람 SSE 연결 요청")
    @Test
    void given_loginUser_when_connectSse_then_returnSseEmitter() throws Exception {
        // given
        Long memberId = 123L;
        SseEmitter sseEmitter = new SseEmitter(TimeUnit.MINUTES.toMillis(30));
        given(alarmService.connectAlarm(eq(memberId))).willReturn(sseEmitter);
        // when
        mvc.perform(get("/api/v1/notis/subscribe")).andExpect(status().isOk());
        // then
    }

    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    @DisplayName("알람 읽음 처리 요청")
    @Test
    void given_alarmIdAndLoginUser_when_loginUserIsOwnerOfAlarm_then_readAlarm() throws Exception {
        //given
        Long alarmId = 1L;
        Long memberId = 123L;
        given(alarmService.isOwnerOfAlarm(eq(alarmId), eq(memberId))).willReturn(true);
        willDoNothing().given(alarmService).readAlarm(eq(alarmId));
        //when
        mvc.perform(put("/api/v1/notis/" + alarmId))
                .andExpect(status().isOk());
        //then
    }
}