// src/main/java/com/icia/delivery/controller/admin/CouponController.java
package com.icia.delivery.controller.admin;

import com.icia.delivery.dto.admin.CouponDTO;
import com.icia.delivery.dto.admin.CouponEntity;
import com.icia.delivery.dto.admin.CouponUsageEntity;
import com.icia.delivery.service.admin.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class CouponController {

    private final CouponService couponService;
    private static final Logger logger = LoggerFactory.getLogger(CouponController.class);

    /**
     * 모든 요청에 'coupon' 객체를 기본적으로 추가
     */
    @ModelAttribute("coupon")
    public CouponDTO getCouponDTO() {
        return new CouponDTO();
    }

    /**
     * 쿠폰 관리 페이지
     */
    @GetMapping("/coupons")
    public String getCoupons(@RequestParam(value = "searchQuery", required = false) String searchQuery,
                             @RequestParam(value = "status", required = false) String status,
                             @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size,
                             Model model) {
        try {
            logger.info("쿠폰 관리 페이지 요청. 검색어: {}, 상태: {}, 페이지: {}, 사이즈: {}", searchQuery, status, page, size);
            List<CouponEntity> coupons = couponService.getAllCoupons();
            model.addAttribute("coupons", coupons);
            model.addAttribute("searchQuery", searchQuery);
            model.addAttribute("status", status);
            model.addAttribute("currentPage", page);
            model.addAttribute("size", size);
            model.addAttribute("content", "coupons");
            logger.info("쿠폰 목록을 성공적으로 조회했습니다. 총 쿠폰 수: {}", coupons.size());
            return "admin/admin";
        } catch (Exception e) {
            logger.error("쿠폰 관리 페이지 로드 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("error", "쿠폰 데이터를 불러오는 중 오류가 발생했습니다.");
            return "admin/admin";
        }
    }

    /**
     * 쿠폰 등록 폼 페이지
     */
    @GetMapping("/coupons/new")
    public String showRegisterForm(Model model) {
        try {
            logger.info("쿠폰 등록 폼 페이지 요청.");
            model.addAttribute("coupon", new CouponDTO()); // 'coupon' 객체 추가
            model.addAttribute("content", "coupons");
            return "admin/admin";
        } catch (Exception e) {
            logger.error("쿠폰 등록 폼 로드 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("error", "쿠폰 등록 폼을 불러오는 중 오류가 발생했습니다.");
            return "admin/admin";
        }
    }

    /**
     * 쿠폰 등록 처리
     */
    @PostMapping("/coupons")
    public String registerCoupon(@Valid @ModelAttribute("coupon") CouponDTO couponDTO,
                                 BindingResult bindingResult,
                                 RedirectAttributes ra) {
        logger.info("쿠폰 등록 요청: {}", couponDTO);
        if (bindingResult.hasErrors()) {
            logger.warn("쿠폰 등록 시 입력 값에 오류가 있습니다.");
            ra.addFlashAttribute("error", "쿠폰 등록에 실패했습니다. 입력 값을 확인해주세요.");
            return "redirect:/admin/coupons";
        }

        try {
            couponService.registerCoupon(couponDTO);
            logger.info("쿠폰이 성공적으로 등록되었습니다. 코드: {}", couponDTO.getCode());
            ra.addFlashAttribute("msg", "쿠폰이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            logger.error("쿠폰 등록 중 오류 발생: {}", e.getMessage(), e);
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/coupons";
    }

    /**
     * 쿠폰 수정 폼 페이지
     */
    @GetMapping("/coupons/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        logger.info("쿠폰 수정 폼 요청. 쿠폰 ID: {}", id);
        try {
            CouponEntity coupon = couponService.getCouponById(id)
                    .orElseThrow(() -> new Exception("수정할 쿠폰을 찾을 수 없습니다."));
            logger.info("쿠폰을 성공적으로 조회했습니다. ID: {}", id);

            // DTO로 변환
            CouponDTO couponDTO = new CouponDTO();
            couponDTO.setId(coupon.getId());
            couponDTO.setCode(coupon.getCode());
            couponDTO.setName(coupon.getName());
            couponDTO.setContent(coupon.getContent());
            couponDTO.setDeductPrice(coupon.getDeductPrice());
            couponDTO.setMinPrice(coupon.getMinPrice());
            couponDTO.setExpiredDate(coupon.getExpiredDate().toLocalDate()); // LocalDate
            couponDTO.setStatus(coupon.getStatus());
            couponDTO.setOrderType(coupon.getOrderType());
            couponDTO.setModifiedDate(coupon.getModifiedDate());

            model.addAttribute("coupon", couponDTO); // 'coupon' 객체 업데이트
            model.addAttribute("content", "coupons");
            return "admin/admin";
        } catch (Exception e) {
            logger.error("쿠폰 수정 폼 로드 중 오류 발생: {}", e.getMessage(), e);
            ra.addFlashAttribute("error", "쿠폰 데이터를 불러오는 중 오류가 발생했습니다.");
            return "redirect:/admin/coupons";
        }
    }

    /**
     * 쿠폰 수정 처리
     */
    @PostMapping("/coupons/update")
    public String updateCoupon(@Valid @ModelAttribute("coupon") CouponDTO couponDTO,
                               BindingResult bindingResult,
                               RedirectAttributes ra) {
        logger.info("쿠폰 수정 요청: {}", couponDTO);
        if (bindingResult.hasErrors()) {
            logger.warn("쿠폰 수정 시 입력 값에 오류가 있습니다.");
            ra.addFlashAttribute("error", "쿠폰 수정에 실패했습니다. 입력 값을 확인해주세요.");
            return "redirect:/admin/coupons";
        }

        try {
            couponService.updateCoupon(couponDTO);
            logger.info("쿠폰이 성공적으로 수정되었습니다. ID: {}", couponDTO.getId());
            ra.addFlashAttribute("msg", "쿠폰이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            logger.error("쿠폰 수정 중 오류 발생: {}", e.getMessage(), e);
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/coupons";
    }

    /**
     * 쿠폰 삭제 처리
     */
    @GetMapping("/coupons/{id}/delete")
    public String deleteCoupon(@PathVariable Long id, RedirectAttributes ra) {
        logger.info("쿠폰 삭제 요청. 쿠폰 ID: {}", id);
        try {
            couponService.deleteCoupon(id);
            logger.info("쿠폰이 성공적으로 삭제되었습니다. ID: {}", id);
            ra.addFlashAttribute("msg", "쿠폰이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            logger.error("쿠폰 삭제 중 오류 발생: {}", e.getMessage(), e);
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/coupons";
    }

    /**
     * 쿠폰 사용 처리
     * (예: 특정 쿠폰을 사용할 때)
     */
    @PostMapping("/coupons/{id}/use")
    public String useCoupon(@PathVariable Long id,
                            RedirectAttributes ra) {
        logger.info("쿠폰 사용 요청. 쿠폰 ID: {}", id);
        try {
            couponService.useCoupon(id);
            logger.info("쿠폰이 성공적으로 사용되었습니다. ID: {}", id);
            ra.addFlashAttribute("msg", "쿠폰이 성공적으로 사용되었습니다.");
        } catch (Exception e) {
            logger.error("쿠폰 사용 중 오류 발생: {}", e.getMessage(), e);
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/coupons";
    }

    /**
     * 쿠폰 사용 기록 조회 페이지
     */
    @GetMapping("/coupons/{id}/usages")
    public String getCouponUsages(@PathVariable Long id, Model model, RedirectAttributes ra) {
        logger.info("쿠폰 사용 기록 조회 요청. 쿠폰 ID: {}", id);
        try {
            List<CouponUsageEntity> usages = couponService.getCouponUsages(id);
            model.addAttribute("usages", usages);
            model.addAttribute("content", "couponUsages");
            logger.info("쿠폰 사용 기록을 성공적으로 조회했습니다. 쿠폰 ID: {}, 기록 수: {}", id, usages.size());
        } catch (Exception e) {
            logger.error("쿠폰 사용 기록 조회 중 오류 발생: {}", e.getMessage(), e);
            ra.addFlashAttribute("error", "쿠폰 사용 기록을 조회하는데 실패했습니다.");
            return "redirect:/admin/coupons";
        }
        return "admin/admin";
    }

    /**
     * JSON으로 쿠폰 정보를 반환하는 메서드 추가
     * (JavaScript fetch를 통해 호출)
     */
    @GetMapping("/coupons/{id}")
    @ResponseBody
    public CouponDTO getCoupon(@PathVariable Long id, RedirectAttributes ra) throws Exception {
        logger.info("쿠폰 정보 요청 (JSON). 쿠폰 ID: {}", id);
        try {
            CouponEntity coupon = couponService.getCouponById(id)
                    .orElseThrow(() -> new Exception("쿠폰을 찾을 수 없습니다."));
            logger.info("쿠폰을 성공적으로 조회했습니다. ID: {}", id);

            // DTO로 변환
            CouponDTO couponDTO = new CouponDTO();
            couponDTO.setId(coupon.getId());
            couponDTO.setCode(coupon.getCode());
            couponDTO.setName(coupon.getName());
            couponDTO.setContent(coupon.getContent());
            couponDTO.setDeductPrice(coupon.getDeductPrice());
            couponDTO.setMinPrice(coupon.getMinPrice());
            couponDTO.setExpiredDate(coupon.getExpiredDate().toLocalDate()); // LocalDate
            couponDTO.setStatus(coupon.getStatus());
            couponDTO.setOrderType(coupon.getOrderType());
            couponDTO.setModifiedDate(coupon.getModifiedDate());

            return couponDTO;
        } catch (Exception e) {
            logger.error("쿠폰 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            ra.addFlashAttribute("error", "쿠폰 데이터를 불러오는 중 오류가 발생했습니다.");
            throw e;
        }
    }
}
