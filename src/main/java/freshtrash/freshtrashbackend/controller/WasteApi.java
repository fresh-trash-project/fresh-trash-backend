package freshtrash.freshtrashbackend.controller;

import freshtrash.freshtrashbackend.dto.WasteDto;
import freshtrash.freshtrashbackend.dto.request.WasteRequest;
import freshtrash.freshtrashbackend.service.WasteServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/wastes")
@RequiredArgsConstructor
public class WasteApi {
    private final WasteServiceInterface wasteService;

    /**
     * 폐기물 단일 조회
     */
    @GetMapping("{wasteId}")
    public ResponseEntity<WasteDto> getWaste(@PathVariable Long wasteId) {
        WasteDto wasteDto = wasteService.getWasteDto(wasteId);
        return ResponseEntity.ok(wasteDto);
    }

    /**
     * 폐기물 등록
     */
    @PostMapping
    public ResponseEntity<WasteDto> addWaste(
            @RequestPart MultipartFile imgFile, @RequestPart @Valid WasteRequest wasteRequest) {
        // TODO: 로그인 유저 정보 추가
        // TODO: WasteRequest Validation Error Exception Handling
        WasteDto wasteDto = wasteService.addWaste(imgFile, wasteRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(wasteDto);
    }

    /**
     * 폐기물 삭제
     */
    @DeleteMapping("/{wasteId}")
    public ResponseEntity<Void> deleteWaste(@PathVariable Long wasteId) {
        // TODO: 작성자와 관리자만 삭제할 수 있음
        wasteService.deleteWaste(wasteId);
        return ResponseEntity.ok(null);
    }
}
