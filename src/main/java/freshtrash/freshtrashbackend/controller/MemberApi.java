package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.UserInfo;
import freshtrash.freshtrashbackend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberApi {
    private final MemberService memberService;

    /**
     * 유저 정보 조회
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<UserInfo> getMember(@PathVariable Long memberId) {
        UserInfo userInfo = UserInfo.fromEntity(memberService.getMemberEntity(memberId));
        return ResponseEntity.ok(userInfo);
    }
}
