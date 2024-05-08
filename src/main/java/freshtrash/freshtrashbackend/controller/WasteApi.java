package freshtrash.freshtrashbackend.controller;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.dto.response.ChatRoomResponse;
import freshtrash.freshtrashbackend.dto.response.WasteResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.entity.Waste;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import freshtrash.freshtrashbackend.exception.ChatRoomException;
import freshtrash.freshtrashbackend.exception.WasteException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.service.ChatRoomService;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/wastes")
public class WasteApi {
    private final WasteService wasteService;
    private final FileService fileService;
    private final ChatRoomService chatRoomService;

    /**
     * 폐기물 단일 조회
     */
    @GetMapping("/{wasteId}")
    public ResponseEntity<WasteResponse> getWaste(@PathVariable Long wasteId) {
        wasteService.updateViewCount(wasteId);
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

    @PutMapping("/{wasteId}")
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
        String savedFileName = wasteService.findFileNameOfWaste(wasteId).fileName();
        wasteService.deleteWaste(wasteId);
        fileService.deleteFileIfExists(savedFileName);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    /**
     * 채팅 요청
     */
    @PostMapping("/{wasteId}/chats")
    public ResponseEntity<ChatRoomResponse> handleChatRoomRequest(
            @PathVariable Long wasteId, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        Waste waste = wasteService.getWaste(wasteId);
        Member seller = waste.getMember();
        checkIfSellerOfWaste(memberPrincipal.id(), seller.getId());

        ChatRoom chatRoom = chatRoomService.getOrCreateChatRoom(seller.getId(), memberPrincipal.id(), wasteId);

        ChatRoomResponse response = ChatRoomResponse.fromEntity(
                chatRoom, waste.getTitle(), seller.getNickname(), memberPrincipal.nickname());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 판매자가 자신이 등록한 폐기물에 대해 채팅을 시도하는 경우 예외 처리
     */
    private void checkIfSellerOfWaste(Long buyerId, Long sellerId) {
        if (sellerId.equals(buyerId)) throw new ChatRoomException(ErrorCode.CANNOT_CHAT_WITH_SELF);
    }

    /**
     * 작성자 또는 관리자가 맞는지 확인
     */
    private void checkIfWriterOrAdmin(MemberPrincipal memberPrincipal, Long wasteId) {
        if (memberPrincipal.getUserRole() != UserRole.ADMIN
                && !wasteService.isWriterOfArticle(wasteId, memberPrincipal.id()))
            throw new WasteException(ErrorCode.FORBIDDEN_WASTE);
    }
}
