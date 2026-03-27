package com.icia.delivery.dto.admin;

import com.icia.delivery.dto.member.MemberEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "coupon_usages") // 실제 테이블 이름에 맞게 수정
public class CouponUsageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private Long id; // 쿠폰 사용 기록의 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpn_id", nullable = false)
    private CouponEntity coupon; // 사용된 쿠폰

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_id", nullable = true) // 관리자 사용 시 null 가능
    private MemberEntity member; // 쿠폰을 사용한 회원

    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt; // 사용 일시
}
