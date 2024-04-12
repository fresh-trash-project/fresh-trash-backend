package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.response.MemberResponse;
import freshtrash.freshtrashbackend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import freshtrash.freshtrashbackend.dto.request.MemberRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberApi {
    private final MemberService memberService;

    /**
     * 유저 정보 조회
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long memberId) {
        MemberResponse memberResponse = MemberResponse.fromEntity(memberService.getMemberEntity(memberId));
        return ResponseEntity.ok(memberResponse);
    }

    /**
     * 유저 정보 수정
     */
    @PutMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable Long memberId, @RequestPart MemberRequest memberRequest, @RequestPart MultipartFile imgFile) {
        MemberResponse memberResponse =
                MemberResponse.fromEntity(memberService.updateMember(memberId, memberRequest, imgFile));
        return ResponseEntity.ok(memberResponse);
    }
}
