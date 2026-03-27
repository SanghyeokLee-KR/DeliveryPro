package com.icia.delivery.dao.admin;

import com.icia.delivery.dto.admin.CouponEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
    Optional<CouponEntity> findByCode(String code);




    @Query(
            value = "SELECT c.* FROM coupons c " +
                    "JOIN coupon_usages cu ON c.cpn_id = cu.cpn_id " +
                    "WHERE cu.mem_id = :memberId",
            countQuery = "SELECT count(*) FROM coupons c " +
                    "JOIN coupon_usages cu ON c.cpn_id = cu.cpn_id " +
                    "WHERE cu.mem_id = :memberId",
            nativeQuery = true
    )
    Page<CouponEntity> findByMemberId(Long memberId, Pageable pageable);
}
