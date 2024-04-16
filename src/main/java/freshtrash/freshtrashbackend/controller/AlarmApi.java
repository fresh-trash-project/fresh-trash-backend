package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.response.AlarmResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.exception.AlarmException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notis")
@RequiredArgsConstructor
public class AlarmApi {
    private final AlarmService alarmService;

    /**
     * 현재 로그인한 사용자에게 온 알람 조회
     */
    // TODO: 이후 프론트와 협의를 통해 페이징 처리가 아닌 리스트로 반환되도록 수정될 수 있습니다
    @GetMapping
    public ResponseEntity<Page<AlarmResponse>> getAlarms(
            @PageableDefault Pageable pageable, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        return ResponseEntity.ok(alarmService.getAlarms(memberPrincipal.id(), pageable));
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
    @GetMapping("/{alarmId}")
    public ResponseEntity<Void> readAlarm(
            @PathVariable Long alarmId, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        checkIfOwnerOfAlarm(alarmId, memberPrincipal.id());
        alarmService.readAlarm(alarmId);
        return ResponseEntity.ok(null);
    }

    /**
     * 로그인한 사용자가 대상 알람의 주인인지 확인
     */
    private void checkIfOwnerOfAlarm(Long alarmId, Long memberId) {
        if (!alarmService.isOwnerOfAlarm(alarmId, memberId)) throw new AlarmException(ErrorCode.FORBIDDEN_ALARM);
    }
}
