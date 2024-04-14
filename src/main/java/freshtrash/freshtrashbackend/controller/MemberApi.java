package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.response.MemberResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
    @GetMapping()
    public ResponseEntity<MemberResponse> getMember(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        MemberResponse memberResponse = MemberResponse.fromEntity(memberService.getMemberEntity(memberPrincipal.id()));
        return ResponseEntity.ok(memberResponse);
    }

    /**
     * 유저 정보 수정
     */
    @PutMapping()
    public ResponseEntity<MemberResponse> updateMember(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @RequestPart MemberRequest memberRequest,
            @RequestPart MultipartFile imgFile) {
        MemberResponse memberResponse =
                MemberResponse.fromEntity(memberService.updateMember(memberPrincipal.id(), memberRequest, imgFile));
        return ResponseEntity.ok(memberResponse);
    }
}
