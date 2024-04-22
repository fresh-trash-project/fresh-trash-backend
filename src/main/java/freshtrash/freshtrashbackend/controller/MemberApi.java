package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.response.MemberResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
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
    @GetMapping
    public ResponseEntity<MemberResponse> getMember(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        MemberResponse memberResponse = MemberResponse.fromEntity(memberService.getMember(memberPrincipal.id()));
        return ResponseEntity.ok(memberResponse);
    }

    /**
     * 유저 정보 수정
     */
    @PutMapping
    public ResponseEntity<MemberResponse> updateMember(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @RequestPart MemberRequest memberRequest,
            @RequestPart MultipartFile imgFile) {
        String oldFile =
                memberService.findFileNameOfMember(memberPrincipal.id()).fileName();
        MemberResponse memberResponse =
                MemberResponse.fromEntity(memberService.updateMember(memberPrincipal.id(), memberRequest, imgFile));

        // 파일이 수정된 경우 -> 이전 파일 삭제
        if (StringUtils.hasText(oldFile) && !oldFile.equals(memberResponse.fileName())) {
            memberService.deleteOldFile(oldFile);
        }
        return ResponseEntity.ok(memberResponse);
    }
}
