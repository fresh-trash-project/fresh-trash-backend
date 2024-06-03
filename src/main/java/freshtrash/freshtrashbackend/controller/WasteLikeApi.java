package freshtrash.freshtrashbackend.controller;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.controller.constants.LikeStatus;
import freshtrash.freshtrashbackend.dto.response.ApiResponse;
import freshtrash.freshtrashbackend.dto.response.WasteResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.QWasteLike;
import freshtrash.freshtrashbackend.entity.WasteLike;
import freshtrash.freshtrashbackend.exception.WasteException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.service.WasteLikeService;
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

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wastes")
public class WasteLikeApi {
    private final WasteService wasteService;
    private final WasteLikeService wasteLikeService;

    @GetMapping("/likes")
    public ResponseEntity<Page<WasteResponse>> getLikedWastes(
            @QuerydslPredicate(root = WasteLike.class) Predicate predicate,
            @PageableDefault(size = 6, sort = "createdAt", direction = DESC) Pageable pageable,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        predicate = QWasteLike.wasteLike.memberId.eq(memberPrincipal.id()).and(predicate);
        Page<WasteResponse> wastes = wasteLikeService.getLikedWastes(predicate, pageable);
        return ResponseEntity.ok(wastes);
    }

    /**
     * 폐기물 관심 추가 또는 삭제
     */
    @PostMapping("/{wasteId}/likes")
    public ResponseEntity<ApiResponse<Boolean>> addOrDeleteWasteLike(
            @RequestParam LikeStatus likeStatus,
            @PathVariable Long wasteId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        checkIfNotWriter(memberPrincipal, wasteId);
        Boolean isLike = likeStatus == LikeStatus.LIKE;
        if (isLike) {
            wasteLikeService.addWasteLike(memberPrincipal.id(), wasteId);
        } else {
            wasteLikeService.deleteWasteLike(memberPrincipal.id(), wasteId);
        }

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.of(isLike));
    }

    /**
     * 작성자인지 확인 (작성자인 경우 관심 추가/삭제 할수 없음)
     */
    private void checkIfNotWriter(MemberPrincipal memberPrincipal, Long wasteId) {
        if (wasteService.isWriterOfArticle(wasteId, memberPrincipal.id())) {
            throw new WasteException(ErrorCode.OWNER_WASTE_CANT_LIKE);
        }
    }
}
