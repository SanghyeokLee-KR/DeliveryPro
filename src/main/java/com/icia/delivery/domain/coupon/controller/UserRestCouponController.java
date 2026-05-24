package com.icia.delivery.domain.coupon.controller;


import com.icia.delivery.domain.coupon.dto.CouponDTO;
import com.icia.delivery.domain.coupon.entity.CouponEntity;
import com.icia.delivery.domain.coupon.service.CouponService;
import com.icia.delivery.global.exception.BusinessException;
import com.icia.delivery.global.exception.ErrorCode;
import com.icia.delivery.global.response.ApiResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
    @GetMapping("/getCoupons")
    public ResponseEntity<ApiResponse<List<CouponDTO>>> getCoupons() {
        Long currentMemberId = getCurrentMemberId();
        List<CouponEntity> entities = couponService.getCouponsByMemberId(currentMemberId);

        // "사용가능" 상태인 쿠폰만 필터링하여 DTO로 변환
        List<CouponDTO> coupons = entities.stream()
                .filter(coupon -> "Y".equals(coupon.getStatus()))
                .map(CouponDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(coupons));
    }


    @PostMapping("/useCoupon")
    public ResponseEntity<ApiResponse<String>> useCoupon(@RequestParam("couponId") Long couponId) {
        try {
            couponService.useUserCoupon(couponId);
            return ResponseEntity.ok(ApiResponse.success("Coupon used."));
        } catch (IllegalStateException e) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to update coupon.", e);
        }
    }

    private Long getCurrentMemberId() {
        Long mId= (Long) session.getAttribute("mem_id");
        if (mId != null) {
            return mId; // 세션에서 가져온 값을 Long 타입으로 변환
        } else {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Login is required.");
        }
    }
}
