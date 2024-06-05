package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.request.MemberRequest;
import freshtrash.freshtrashbackend.dto.response.MemberResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.service.FileService;
import freshtrash.freshtrashbackend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberApi {
    private final MemberService memberService;
    private final FileService fileService;

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
            @RequestPart(required = false) MultipartFile imgFile) {
        String oldFileName =
                memberService.findFileNameOfMember(memberPrincipal.id()).fileName();
        MemberResponse memberResponse =
                MemberResponse.fromEntity(memberService.updateMember(memberPrincipal, memberRequest, imgFile));
        fileService.deleteOrNotOldFile(oldFileName, memberResponse.fileName());
        return ResponseEntity.ok(memberResponse);
    }
}
