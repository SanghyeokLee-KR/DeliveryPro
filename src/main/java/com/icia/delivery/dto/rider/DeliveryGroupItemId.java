package com.icia.delivery.dto.rider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DeliveryGroupItemId
 * 
 * - (delivery_id, order_id) 복합키를 표현할 클래스
 * - @IdClass로 사용될 때, Serializable 구현 필요
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryGroupItemId implements Serializable {
    private Long deliveryId;
    private Long orderId;

    // equals, hashCode는 lombok @Data가 자동 생성(기본) 
    // → 또는 @EqualsAndHashCode manually
}
