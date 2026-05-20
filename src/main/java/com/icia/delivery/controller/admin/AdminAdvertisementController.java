package com.icia.delivery.controller.admin;

import com.icia.delivery.dto.admin.AdvertisementDTO;
import com.icia.delivery.service.admin.AdvertisementService;
import lombok.RequiredArgsConstructor;
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/advertisements";
    }

    // 광고 단건 조회 (수정 모달용 AJAX)
    @ResponseBody
    @GetMapping("/{advId}")
    public AdvertisementDTO getAdvertisement(@PathVariable Long advId) {
        return adService.getAdvertisement(advId);
    }
}
