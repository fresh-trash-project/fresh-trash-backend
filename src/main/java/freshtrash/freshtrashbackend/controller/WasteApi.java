package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.WasteDto;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.dto.response.ApiResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.constants.LikeStatus;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import freshtrash.freshtrashbackend.exception.WasteException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.service.WasteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/v1/wastes")
@RequiredArgsConstructor
public class WasteApi {
    private final WasteService wasteService;

    /**
     * 폐기물 단일 조회
     */
    @GetMapping("{wasteId}")
    public ResponseEntity<WasteDto> getWaste(@PathVariable Long wasteId) {
        WasteDto wasteDto = WasteDto.fromEntity(wasteService.getWasteEntity(wasteId));
        return ResponseEntity.ok(wasteDto);
    }

    /**
     * 폐기물 목록 조회
     */
    @GetMapping
    public ResponseEntity<Page<WasteDto>> getWastes(
            @PageableDefault(size = 5, sort = "createdAt", direction = DESC) Pageable pageable) {
        Page<WasteDto> wastes = wasteService.getWastes(pageable);
        return ResponseEntity.ok(wastes);
    }

    /**
     * 폐기물 등록
     */
    @PostMapping
    public ResponseEntity<WasteDto> addWaste(
            @RequestPart MultipartFile imgFile,
            @RequestPart @Valid WasteRequest wasteRequest,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        WasteDto wasteDto = wasteService.addWaste(imgFile, wasteRequest, memberPrincipal);
        return ResponseEntity.status(HttpStatus.CREATED).body(wasteDto);
    }

    @PutMapping("{wasteId}")
    public ResponseEntity<WasteDto> updateWaste(
            @RequestPart MultipartFile imgFile,
            @RequestPart @Valid WasteRequest wasteRequest,
            @PathVariable Long wasteId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        checkIfWriterOrAdmin(memberPrincipal, wasteId);
        WasteDto wasteDto = wasteService.updateWaste(
                imgFile, wasteRequest, wasteService.findFileNameOfWaste(wasteId), memberPrincipal);
        return ResponseEntity.ok(wasteDto);
    }

    /**
     * 폐기물 삭제
     */
    @DeleteMapping("/{wasteId}")
    public ResponseEntity<Void> deleteWaste(
            @PathVariable Long wasteId, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        checkIfWriterOrAdmin(memberPrincipal, wasteId);
        wasteService.deleteWaste(wasteId, wasteService.findFileNameOfWaste(wasteId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    /**
     * 작성자 또는 관리자가 맞는지 확인
     */
    private void checkIfWriterOrAdmin(MemberPrincipal memberPrincipal, Long wasteId) {
        if (memberPrincipal.getUserRole() != UserRole.ADMIN
                && !wasteService.isWriterOfArticle(wasteId, memberPrincipal.id()))
            throw new WasteException(ErrorCode.FORBIDDEN_WASTE);
    }

    /**
     * 폐기물 관심 추가 또는 삭제
     */
    @PostMapping("/{wasteId}/likes")
    public ResponseEntity<?> addOrDeleteWasteLike(
            @RequestBody LikeStatus likeStatus,
            @PathVariable Long wasteId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        // 본인 글인지 확인
        checkIfNotWriter(memberPrincipal, wasteId);
        int likeCount = wasteService.addOrDeleteWasteLike(likeStatus, memberPrincipal.id(), wasteId);

        return ResponseEntity.ok(ApiResponse.of(likeCount));
    }

    /**
     * 작성자가 아닌지 확인
     */
    private void checkIfNotWriter(MemberPrincipal memberPrincipal, Long wasteId) {
        if (wasteService.isWriterOfArticle(wasteId, memberPrincipal.id())) {
            throw new WasteException(ErrorCode.OWNER_WASTE_CANT_LIKE);
        }
    }
}
