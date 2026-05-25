package com.icia.delivery.domain.admin.controller;

import com.icia.delivery.domain.admin.dto.AdvertisementDTO;
import com.icia.delivery.domain.admin.service.AdvertisementService;
import com.icia.delivery.global.exception.BusinessException;
import com.icia.delivery.global.exception.ErrorCode;
import com.icia.delivery.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/advertisements")
@RequiredArgsConstructor
public class AdminAdvertisementController {

    private static final Logger log = LoggerFactory.getLogger(AdminAdvertisementController.class);

    private final AdvertisementService adService;

    @GetMapping
    public String advertisementList(Model model) {
        List<AdvertisementDTO> advList = adService.getAllAdvertisements();
        model.addAttribute("advList", advList);
        model.addAttribute("content", "advertisements");
        return "admin/admin";
    }

    // 광고 등록
    @PostMapping
    public String createAdvertisement(@RequestParam String advTitle,
                                      @RequestParam("advImageFile") MultipartFile advImageFile,
                                      RedirectAttributes ra) {
        try {
            adService.createAdvertisement(advTitle, advImageFile);
            ra.addFlashAttribute("msg", "광고 등록 성공");
        } catch (Exception e) {
            log.error("Failed to create advertisement. title={}", advTitle, e);
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/advertisements";
    }

    // 광고 수정 (파일 업로드도 가능)
    @PostMapping("/update")
    public String updateAdvertisement(@ModelAttribute AdvertisementDTO dto,
                                      @RequestParam(name="advImageFile", required=false) MultipartFile advImageFile,
                                      RedirectAttributes ra) {
        try {
            adService.updateAdvertisement(dto, advImageFile);
            ra.addFlashAttribute("msg", "광고 수정 완료");
        } catch (Exception e) {
            log.error("Failed to update advertisement. advId={}", dto.getAdvId(), e);
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/advertisements";
    }

    // 광고 삭제
    @GetMapping("/{advId}/delete")
    public String deleteAdvertisement(@PathVariable Long advId,
                                      RedirectAttributes ra) {
        try {
            adService.deleteAdvertisement(advId);
            ra.addFlashAttribute("msg", "광고 삭제 완료");
        } catch (Exception e) {
            log.error("Failed to delete advertisement. advId={}", advId, e);
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/advertisements";
    }

    // 광고 단건 조회 (수정 모달용 AJAX)
    @ResponseBody
    @GetMapping("/{advId}")
    public ResponseEntity<ApiResponse<AdvertisementDTO>> getAdvertisement(@PathVariable Long advId) {
        try {
            return ResponseEntity.ok(ApiResponse.success(adService.getAdvertisement(advId)));
        } catch (BusinessException e) {
            ErrorCode errorCode = e.getErrorCode();
            log.warn("Failed to load advertisement. ID: {}, code={}", advId, errorCode.getCode(), e);
            return ResponseEntity
                    .status(errorCode.getHttpStatus())
                    .body(ApiResponse.<AdvertisementDTO>fail(errorCode, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Failed to load advertisement. ID: {}", advId, e);
            return ResponseEntity
                    .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                    .body(ApiResponse.<AdvertisementDTO>fail(ErrorCode.INTERNAL_SERVER_ERROR, "광고 정보를 불러오는 중 오류가 발생했습니다.", null));
        }
    }
}
