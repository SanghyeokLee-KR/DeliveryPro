package com.icia.delivery.controller.admin;

import com.icia.delivery.dto.admin.CouponEntity;
import com.icia.delivery.service.admin.CouponService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserCouponController {

    private final CouponService couponService;
    private final HttpSession session;

    /**
     * 사용자 쿠폰 페이지
     */
    @Transactional
    @GetMapping("/coupon")
    public String getUserCoupons(
            @RequestParam(defaultValue = "0") int page,            // 페이징 시 현재 페이지 번호
            @RequestParam(defaultValue = "10") int size,   // 페이징 시 한 페이지 당 항목 수
            @RequestParam(defaultValue = "cpn_id") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,    // 정렬 방향 (asc 또는 desc)
            Model model) {

        Long currentMemberId = getCurrentMemberId();
        List<CouponEntity> coupons;

        // 페이징 관련 파라미터가 모두 전달된 경우에만 페이징 처리
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // 페이징 처리된 쿠폰 목록 조회 (서비스에서 Page<CouponEntity> 반환)
        Page<CouponEntity> couponsPage = couponService.getUserCoupons(currentMemberId, pageable);
        coupons = couponsPage.getContent();

        // 페이징 정보를 모델에 추가
        model.addAttribute("coupon", couponsPage.getContent());
        model.addAttribute("couponsPage", couponsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", couponsPage.getTotalPages());
        model.addAttribute("totalElements", couponsPage.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equalsIgnoreCase("asc") ? "desc" : "asc");


        // 조회된 쿠폰 목록 로그 출력
        System.out.println("현재 로그인한 사용자 ID: " + currentMemberId);
        System.out.println("조회된 쿠폰 개수: " + coupons.size());
        for (CouponEntity coupon : coupons) {
            System.out.println("쿠폰 코드: " + coupon.getCode());
        }

        model.addAttribute("coupons", coupons);
        return "member/coupon"; // Thymeleaf 템플릿 반환
    }

    /**
     * 사용자 쿠폰 등록 처리
     */
    @PostMapping("/coupon")
    public String registerUserCoupon(@RequestParam("code") String code,
                                     RedirectAttributes ra) {
        try {
            couponService.registerUserCoupon(code, getCurrentMemberId()); // 구현 필요
            ra.addFlashAttribute("msg", "쿠폰이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coupon";
    }

    /**
     * 사용자 쿠폰 사용 처리
     */
    @PostMapping("/coupon/{id}/use")
    public String useUserCoupon(@PathVariable Long id,
                                RedirectAttributes ra) {
        try {
            couponService.useCoupon(id, getCurrentMemberId()); // 구현 필요
            ra.addFlashAttribute("msg", "쿠폰이 성공적으로 사용되었습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coupon";
    }

    /**
     * 현재 사용자 ID를 가져오는 메서드 (구현 필요)
     */

    private Long getCurrentMemberId() {
        Long mId= (Long) session.getAttribute("mem_id");
        if (mId != null) {
            return mId; // 세션에서 가져온 값을 Long 타입으로 변환
        } else {
            throw new IllegalStateException("로그인한 사용자가 없습니다.");
        }
    }
}
