package com.icia.delivery.dto.rider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DeliveryGroupDTO
 *
 * - DeliveryGroupEntity와 1:1 대응되는 DTO
 * - Controller/Service 계층 등에서 입출력에 사용
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryGroupDTO {

    private Long deliveryId;         // PK
    private Long riderNo;            // 라이더 번호 (FK)
    private Long storeId;            // 가게 ID
    private String deliveryType;     // 한집배달 또는 묶음배달
    private String deliveryStatus;   // 배달전, 픽업중, 배달중, 배달완료, 취소
    private String customerRequest;  // 고객 요청사항
    private Integer deliveryFee;     // 배달비
    private LocalDateTime createdAt; // 생성 시각
    private LocalDateTime updatedAt; // 수정 시각
    private LocalDateTime callTime;  // 호출 시각 (call_time)

    /**
     * Entity -> DTO 변환
     */
    public static DeliveryGroupDTO toDTO(DeliveryGroupEntity entity) {
        DeliveryGroupDTO dto = new DeliveryGroupDTO();
        dto.setDeliveryId(entity.getDeliveryId());
        dto.setRiderNo(entity.getRiderNo());
        dto.setStoreId(entity.getStoreId());
        dto.setDeliveryType(entity.getDeliveryType());
        dto.setDeliveryStatus(entity.getDeliveryStatus());
        dto.setCustomerRequest(entity.getCustomerRequest());
        dto.setDeliveryFee(entity.getDeliveryFee());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCallTime(entity.getCallTime());
        return dto;
    }

    /**
     * DTO -> Entity 변환
     */
    public static DeliveryGroupEntity toEntity(DeliveryGroupDTO dto) {
        DeliveryGroupEntity entity = new DeliveryGroupEntity();
        entity.setDeliveryId(dto.getDeliveryId());
        entity.setRiderNo(dto.getRiderNo());
        entity.setStoreId(dto.getStoreId());
        entity.setDeliveryType(dto.getDeliveryType());
        entity.setDeliveryStatus(dto.getDeliveryStatus());
        entity.setCustomerRequest(dto.getCustomerRequest());
        entity.setDeliveryFee(dto.getDeliveryFee());
        // createdAt 기본값 처리
        entity.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setCallTime(dto.getCallTime());
        return entity;
    }
}
