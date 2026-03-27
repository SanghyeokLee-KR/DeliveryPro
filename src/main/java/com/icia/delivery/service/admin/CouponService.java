package com.icia.delivery.service.admin;

import com.icia.delivery.dto.admin.CouponDTO;
import com.icia.delivery.dto.admin.CouponEntity;
import com.icia.delivery.dto.admin.CouponUsageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CouponService {

    // 관리자 측 기능
    void registerCoupon(CouponDTO couponDTO) throws Exception;
    void updateCoupon(CouponDTO couponDTO) throws Exception;
    void deleteCoupon(Long couponId) throws Exception;
    List<CouponEntity> getAllCoupons();
    Optional<CouponEntity> getCouponById(Long couponId);
    void useCoupon(Long couponId) throws Exception;
    List<CouponUsageEntity> getCouponUsages(Long couponId) throws Exception;

    // 사용자 측 기능
    List<CouponEntity> getCouponsByMemberId(Long memberId);
    void registerUserCoupon(String code, Long memberId) throws Exception;
    void useCoupon(Long couponId, Long memberId) throws Exception;
    List<CouponUsageEntity> getCouponUsagesByMemberId(Long memberId);
    Page<CouponEntity> getUserCoupons(Long currentMemberId, Pageable pageable);

    void useUserCoupon(Long couponId) throws Exception;
}
