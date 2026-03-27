package com.icia.delivery.controller.admin;


import com.icia.delivery.dto.admin.CouponDTO;
import com.icia.delivery.dto.admin.CouponEntity;
import com.icia.delivery.service.admin.CouponService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class UserRestCouponController {

    private final CouponService couponService;
    private final HttpSession session;

    // 회원별 쿠폰을 조회하는 엔드포인트 (GET 방식 예시)
    @Transactional
    @GetMapping("/getCoupons")
    public List<CouponDTO> getCoupons() {
        Long currentMemberId = getCurrentMemberId();
        List<CouponEntity> entities = couponService.getCouponsByMemberId(currentMemberId);

        // "사용가능" 상태인 쿠폰만 필터링하여 DTO로 변환
        return entities.stream()
                .filter(coupon -> "Y".equals(coupon.getStatus()))
                .map(CouponDTO::toDTO)
                .collect(Collectors.toList());
    }


    @PostMapping("/useCoupon")
    public ResponseEntity<?> useCoupon(@RequestParam("couponId") Long couponId) {
        try {
            couponService.useUserCoupon(couponId);
            return ResponseEntity.ok("쿠폰 사용 완료");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("쿠폰 업데이트에 실패하였습니다.");
        }
    }

    private Long getCurrentMemberId() {
        Long mId= (Long) session.getAttribute("mem_id");
        if (mId != null) {
            return mId; // 세션에서 가져온 값을 Long 타입으로 변환
        } else {
            throw new IllegalStateException("로그인한 사용자가 없습니다.");
        }
    }
}