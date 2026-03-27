package com.icia.delivery.dto.rider;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DeliveryGroupEntity
 *
 * - DB 테이블: delivery_group
 * - 한 번의 배달(한집/묶음)에 관한 전반적 정보
 * - 예) 라이더, 배달 유형, 배달 상태, 고객 요청사항, 배달비 등
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "delivery_group")
public class DeliveryGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long deliveryId;               // PK: 배달 그룹 ID

    @Column(name = "rider_no", nullable = false)
    private Long riderNo;                  // 라이더 (FK)

    @Column(name = "store_id", nullable = false)
    private Long storeId;                  // 가게 ID

    @Column(name = "delivery_type", nullable = false, length = 20)
    private String deliveryType;           // '한집배달' or '묶음배달'

    @Column(name = "delivery_status", nullable = false, length = 50)
    private String deliveryStatus;         // 배달 상태 (배달전, 픽업중, 배달중, 배달완료, 취소)

    @Column(name = "customer_request", length = 500)
    private String customerRequest;        // 고객 요청사항

    @Column(name = "delivery_fee")
    private Integer deliveryFee;           // 배달비

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // 생성 시각

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;                      // 수정 시각

    // 새로 추가된 호출 시각 필드 (DB의 call_time 컬럼)
    @Column(name = "call_time")
    private LocalDateTime callTime;

    /**
     * Entity -> DTO 변환 메서드
     */
    public static DeliveryGroupDTO toDTO(DeliveryGroupEntity entity) {
        return DeliveryGroupDTO.toDTO(entity);
    }

    /**
     * DTO -> Entity 변환 메서드
     */
    public static DeliveryGroupEntity toEntity(DeliveryGroupDTO dto) {
        return DeliveryGroupDTO.toEntity(dto);
    }
}
