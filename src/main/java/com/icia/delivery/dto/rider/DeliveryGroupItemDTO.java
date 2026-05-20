package com.icia.delivery.dto.rider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DeliveryGroupItemDTO
 *
 * - DeliveryGroupItemEntity와 1:1 대응되는 DTO
 * - (delivery_id + order_id) 복합키
 * - storeAddress, destinationAddress 등 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryGroupItemDTO {

    private Long deliveryId;         // FK -> delivery_group
    private Long orderId;            // FK -> orders
    private Integer orderSequence;   // 묶음배달 순서 (옵션)
    private String storeAddress;     // 매장 주소
    private String destinationAddress; // 고객 배송지 주소

    /**
     * Entity -> DTO
     */
    public static DeliveryGroupItemDTO toDTO(DeliveryGroupItemEntity entity) {
        DeliveryGroupItemDTO dto = new DeliveryGroupItemDTO();
        dto.setDeliveryId(entity.getDeliveryId());
        dto.setOrderId(entity.getOrderId());
        dto.setOrderSequence(entity.getOrderSequence());
        dto.setStoreAddress(entity.getStoreAddress());
        dto.setDestinationAddress(entity.getDestinationAddress());
        return dto;
    }

    /**
     * DTO -> Entity
     */
    public static DeliveryGroupItemEntity toEntity(DeliveryGroupItemDTO dto) {
        DeliveryGroupItemEntity entity = new DeliveryGroupItemEntity();
        entity.setDeliveryId(dto.getDeliveryId());
        entity.setOrderId(dto.getOrderId());
        entity.setOrderSequence(dto.getOrderSequence());
        entity.setStoreAddress(dto.getStoreAddress());
        entity.setDestinationAddress(dto.getDestinationAddress());
        return entity;
    }
}
