package freshtrash.freshtrashbackend.domain.alarm.controller;

import freshtrash.freshtrashbackend.domain.alarm.dto.response.AlarmResponse;
import freshtrash.freshtrashbackend.domain.member.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.domain.alarm.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notis")
public class AlarmController {
    private final AlarmService alarmService;

    /**
     * 현재 로그인한 사용자에게 온 알람 조회
     */
    @GetMapping
    public ResponseEntity<Page<AlarmResponse>> getAlarms(
            @RequestParam Boolean isRead,
            @PageableDefault Pageable pageable,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        return ResponseEntity.ok(alarmService.getAlarms(memberPrincipal.id(), isRead, pageable));
    }

    /**
     * SSE 연결 요청
     */
    @GetMapping("/subscribe")
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        return ResponseEntity.ok(alarmService.connectAlarm(memberPrincipal.id()));
    }

    /**
     * 알람 읽음 처리 요청
     */
    @PutMapping("/{alarmId}")
    public ResponseEntity<Void> readAlarm(
            @PathVariable Long alarmId, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        alarmService.readAlarm(alarmId, memberPrincipal.id());
        return ResponseEntity.ok(null);
    }
}
