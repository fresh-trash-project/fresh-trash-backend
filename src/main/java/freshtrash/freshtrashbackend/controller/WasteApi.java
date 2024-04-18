package freshtrash.freshtrashbackend.controller;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.dto.constants.LikeStatus;
import freshtrash.freshtrashbackend.dto.response.WasteResponse;
import freshtrash.freshtrashbackend.dto.response.ReviewResponse;
import freshtrash.freshtrashbackend.dto.request.ReviewRequest;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.dto.response.ApiResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import freshtrash.freshtrashbackend.exception.WasteException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.service.FileService;
import freshtrash.freshtrashbackend.service.WasteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
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
    private final FileService fileService;

    /**
     * 폐기물 단일 조회
     */
    @GetMapping("{wasteId}")
    public ResponseEntity<WasteResponse> getWaste(@PathVariable Long wasteId) {
        WasteResponse wasteResponse = WasteResponse.fromEntity(wasteService.getWaste(wasteId));
        return ResponseEntity.ok(wasteResponse);
    }

    /**
     * 폐기물 목록 조회
     * @param district 읍면동
     * @param predicate 제목 검색 (e.g. ?title={검색 키워드})
     */
    @GetMapping
    public ResponseEntity<Page<WasteResponse>> getWastes(
            @RequestParam(required = false) String district,
            @QuerydslPredicate(root = Waste.class) Predicate predicate,
            @PageableDefault(size = 6, sort = "createdAt", direction = DESC) Pageable pageable) {
        Page<WasteResponse> wastes = wasteService.getWastes(district, predicate, pageable);
        return ResponseEntity.ok(wastes);
    }

    /**
     * 폐기물 등록
     */
    @PostMapping
    public ResponseEntity<WasteResponse> addWaste(
            @RequestPart MultipartFile imgFile,
            @RequestPart @Valid WasteRequest wasteRequest,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        WasteResponse wasteResponse = wasteService.addWaste(imgFile, wasteRequest, memberPrincipal);
        return ResponseEntity.status(HttpStatus.CREATED).body(wasteResponse);
    }

    @PutMapping("{wasteId}")
    public ResponseEntity<WasteResponse> updateWaste(
            @RequestPart MultipartFile imgFile,
            @RequestPart @Valid WasteRequest wasteRequest,
            @PathVariable Long wasteId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        checkIfWriterOrAdmin(memberPrincipal, wasteId);

        String savedFileName = wasteService.findFileNameOfWaste(wasteId).fileName();
        WasteResponse wasteResponse = wasteService.updateWaste(wasteId, imgFile, wasteRequest, memberPrincipal);
        fileService.deleteFileIfExists(savedFileName);

        return ResponseEntity.ok(wasteResponse);
    }

    /**
     * 폐기물 삭제
     */
    @DeleteMapping("/{wasteId}")
    public ResponseEntity<Void> deleteWaste(
            @PathVariable Long wasteId, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        checkIfWriterOrAdmin(memberPrincipal, wasteId);
        wasteService.deleteWaste(wasteId);
        fileService.deleteFileIfExists(wasteService.findFileNameOfWaste(wasteId).fileName());

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
     * 폐기물 리뷰 작성
     */
    @PostMapping("/{wasteId}/reviews")
    private ResponseEntity<ReviewResponse> addWasteReview(
            @RequestBody @Valid ReviewRequest reviewRequest,
            @PathVariable Long wasteId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        // TODO : transaction 거래 확인

        ReviewResponse reviewResponse =
                ReviewResponse.fromEntity(wasteService.insertWasteReview(reviewRequest, wasteId, memberPrincipal.id()));
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewResponse);
    }

    /**
     * 폐기물 관심 추가 또는 삭제
     */
    @PostMapping("/{wasteId}/likes")
    public ResponseEntity<ApiResponse<Integer>> addOrDeleteWasteLike(
            @RequestParam LikeStatus likeStatus,
            @PathVariable Long wasteId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        checkIfNotWriter(memberPrincipal, wasteId);

        // TODO likeCount가 변경된 관심수를 반환하도록 변경 예정

        // 관심 추가 또는 삭제
        int likeCount = wasteService.addOrDeleteWasteLike(likeStatus, memberPrincipal.id(), wasteId);

        return ResponseEntity.ok(ApiResponse.of(likeCount));
    }

    /**
     * 작성자가 아닌지 확인 (작성자인 경우 관심 추가/삭제 할수 없음)
     */
    private void checkIfNotWriter(MemberPrincipal memberPrincipal, Long wasteId) {
        if (wasteService.isWriterOfArticle(wasteId, memberPrincipal.id())) {
            throw new WasteException(ErrorCode.OWNER_WASTE_CANT_LIKE);
        }
    }
}
