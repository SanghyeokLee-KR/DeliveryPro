package com.icia.delivery.dto.admin;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "coupons") // 실제 테이블 이름에 맞게 수정
public class CouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cpn_id")
    private Long id; // 쿠폰 고유 ID

    @Column(name = "cpn_code", nullable = false, unique = true, length = 255)
    private String code; // 쿠폰 코드

    @Column(name = "cpn_name", nullable = false, length = 255)
    private String name; // 쿠폰 이름

    @Column(name = "cpn_content", nullable = false, length = 255)
    private String content; // 쿠폰 내용

    @Column(name = "cpn_deduct_price", nullable = false)
    private Long deductPrice; // 할인 금액

    @Column(name = "cpn_min_price", nullable = false)
    private Long minPrice; // 최소 주문 금액

    @Column(name = "cpn_order_type", nullable = false, length = 255)
    private String orderType; // 주문 유형 (예: 배달)

    @Column(name = "cpn_status", nullable = false, length = 255)
    private String status; // 쿠폰 상태 (Y: 활성, N: 비활성, E: 만료)

    @Column(name = "cpn_created", nullable = false, updatable = false)
    private LocalDateTime createdDate; // 생성 일시

    @Column(name = "cpn_modified", nullable = false)
    private LocalDateTime modifiedDate; // 수정 일시

    @Column(name = "cpn_expired", nullable = false)
    private LocalDateTime expiredDate; // 만료 일시

    @OneToMany(mappedBy = "coupon", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CouponUsageEntity> usages; // 쿠폰 사용 기록

    /**
     * 엔티티가 생성되기 전에 호출되어 createdDate와 modifiedDate를 설정합니다.
     */
    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
    }

    /**
     * 엔티티가 업데이트되기 전에 호출되어 modifiedDate를 설정합니다.
     */
    @PreUpdate
    protected void onUpdate() {
        this.modifiedDate = LocalDateTime.now();
    }


    public static CouponEntity toEntity(CouponDTO dto){
        CouponEntity entity = new CouponEntity();
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setContent(dto.getContent());
        entity.setDeductPrice(dto.getDeductPrice());
        entity.setMinPrice(dto.getMinPrice());
        entity.setOrderType(dto.getOrderType());
        entity.setStatus(dto.getStatus());
        return entity;
    }
}
