// src/main/java/com/icia/delivery/dao/admin/CouponUsageRepository.java
package com.icia.delivery.dao.admin;

import com.icia.delivery.dto.admin.CouponUsageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsageEntity, Long> {

    /**
     * 특정 회원이 사용한 모든 쿠폰 사용 기록을 조회합니다.
     *
     * @param memberId 회원의 고유 ID (mId)
     * @return 회원이 사용한 쿠폰 사용 기록 목록
     */
    @Query("SELECT u FROM CouponUsageEntity u WHERE u.member.mId = :memberId")
    List<CouponUsageEntity> findByMemberId(@Param("memberId") Long memberId);

    /**
     * 특정 쿠폰에 대한 모든 사용 기록을 조회합니다.
     *
     * @param couponId 쿠폰의 고유 ID
     * @return 쿠폰에 대한 사용 기록 목록
     */
    @Query("SELECT u FROM CouponUsageEntity u WHERE u.coupon.id = :couponId")
    List<CouponUsageEntity> findByCouponId(@Param("couponId") Long couponId);

    /**
     * 특정 회원이 특정 쿠폰을 이미 사용했는지 확인합니다.
     *
     * @param couponId 쿠폰의 고유 ID
     * @param memberId 회원의 고유 ID (mId)
     * @return 사용 여부 (true: 이미 사용함, false: 사용하지 않음)
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM CouponUsageEntity u WHERE u.coupon.id = :couponId AND u.member.mId = :memberId")
    boolean existsByCouponIdAndMemberId(@Param("couponId") Long couponId, @Param("memberId") Long memberId);
}
