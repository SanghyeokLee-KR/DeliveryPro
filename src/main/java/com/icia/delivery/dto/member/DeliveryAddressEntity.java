package com.icia.delivery.dto.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_address")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Oracle의 IDENTITY 컬럼에 맞게 설정
    @Column(name = "addr_id")
    private Long addrId; // 배송지 ID

    @Column(name = "addr_member_id", nullable = false)
    private Long addrMemberId; // 회원 ID

    @Column(name = "addr_name", nullable = false)
    private String addrName; // 배송지 이름 (예: 집, 회사)

    @Column(name = "addr_address", nullable = false)
    private String addrAddress; // 기본 주소

    @Column(name = "addr_is_main", nullable = false)
    private String addrIsMain = "서브"; // 기본 배송지 여부 ("메인", "서브")

    @Column(name = "addr_register_date", nullable = false, updatable = false)
    private LocalDateTime addrRegisterDate; // 등록 일자

    /**
     * DTO -> Entity 변환
     */
    public static DeliveryAddressEntity toEntity(DeliveryAddressDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DeliveryAddressDTO는 null일 수 없습니다.");
        }

        DeliveryAddressEntity entity = new DeliveryAddressEntity();
        entity.setAddrId(dto.getId());
        entity.setAddrMemberId(dto.getMemberId());
        entity.setAddrName(dto.getName());
        entity.setAddrAddress(dto.getAddress());
        entity.setAddrIsMain(dto.getIsMain());
        // addrRegisterDate는 @PrePersist에서 자동으로 설정됨
        return entity;
    }

    @PrePersist
    public void prePersist() {
        this.addrRegisterDate = LocalDateTime.now();
        if (this.addrIsMain == null) {
            this.addrIsMain = "서브"; // 기본값: 서브
        }
    }
}
