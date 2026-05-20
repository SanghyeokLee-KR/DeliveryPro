package com.icia.delivery.dto.rider;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DeliveryGroupItemEntity
 * 
 * - DB 테이블: delivery_group_item
 * - 복합 PK: (delivery_id + order_id)
 * - 한 배달(delivery_id)에 여러 orders가 묶일 수 있음 (묶음배달)
 * - storeAddress, destinationAddress 등 각 주문의 매장/배송지 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "delivery_group_item")
/** 
 * 복합 PK (deliveryId, orderId) 
 * -> @IdClass(DeliveryGroupItemId) 사용 
 */
@IdClass(DeliveryGroupItemId.class)
public class DeliveryGroupItemEntity {

    @Id
    @Column(name = "delivery_id", nullable = false)
    private Long deliveryId;   // FK -> delivery_group(delivery_id)

    @Id
    @Column(name = "order_id", nullable = false)
    private Long orderId;      // FK -> orders(order_id)

    @Column(name = "order_sequence")
    private Integer orderSequence;  // 묶음배달 시 순서

    @Column(name = "store_address", length = 255)
    private String storeAddress;    // 매장 주소

    @Column(name = "destination_address", length = 255)
    private String destinationAddress; // 고객 배송지 주소

    // (선택) created_at, updated_at이 필요하면 추가
    // @Column(name="created_at")
    // private LocalDateTime createdAt = LocalDateTime.now();

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
