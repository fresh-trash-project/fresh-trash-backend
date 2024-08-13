package freshtrash.freshtrashbackend.domain.member.controller;

import freshtrash.freshtrashbackend.domain.member.dto.request.ChangePasswordRequest;
import freshtrash.freshtrashbackend.domain.member.dto.request.MemberRequest;
import freshtrash.freshtrashbackend.domain.member.dto.response.MemberResponse;
import freshtrash.freshtrashbackend.domain.member.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.domain.member.service.MemberService;
import freshtrash.freshtrashbackend.global.infra.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {
    private final MemberService memberService;
    private final FileService fileService;

    /**
     * 유저 정보 조회
     */
    @GetMapping
    public ResponseEntity<MemberResponse> getMember(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        MemberResponse memberResponse = MemberResponse.fromEntity(memberService.getMemberById(memberPrincipal.id()));
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

    /**
     * 비밀번호 변경
     */
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        memberService.changePassword(changePasswordRequest, memberPrincipal);
        return ResponseEntity.ok(null);
    }
}
