package com.icia.delivery.dao.member;

import com.icia.delivery.dto.member.DeliveryAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddressEntity, Long> {
    // 회원 ID로 배송지 목록 조회
    List<DeliveryAddressEntity> findByAddrMemberId(Long memberId);

    // 기존 메인 주소를 서브로 변경
    @Modifying
    @Query("UPDATE DeliveryAddressEntity da SET da.addrIsMain = '서브' WHERE da.addrMemberId = :memberId AND da.addrIsMain = '메인'")
    void clearMainAddress(@Param("memberId") Long memberId);

    // 메인 주소 조회를 위한 커스텀 쿼리 추가
    @Query("SELECT da FROM DeliveryAddressEntity da WHERE da.addrMemberId = :memberId AND da.addrIsMain = '메인'")
    Optional<DeliveryAddressEntity> findMainAddress(@Param("memberId") Long memberId);
}
