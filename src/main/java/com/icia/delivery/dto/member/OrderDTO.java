package com.icia.delivery.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long orderId;
    private Long prestoId;
    private Long memId;
    private Long menuId;
    private String orderStatus;
    private double orderTotalPrice;
    private String customerMessage;
    private String deliveryMessage;
    private String deliveryType;
    private String paymentMethod;
    private String deliveryStatus;
    private int deliveryFee;
    private int discountAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderCreatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderUpdatedAt;

    private List<OrderItemDTO> orderItems = new ArrayList<>(); // Null 대신 빈 리스트로 초기화

    private String userAddress;
    private String storeAddress;

    // 추가: 묶음 배달의 순서 (order_sequence)
    private Integer orderSequence;

    // 추가: 호출 시각 (DeliveryGroupEntity의 call_time 컬럼 기준)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime callTime;

    // 묶음 배달의 경우 그룹(DeliveryGroup)의 ID
    private Long deliveryId;

    public static OrderDTO toDTO(OrderEntity entity) {
        OrderDTO dto = new OrderDTO();

        dto.setOrderId(entity.getOrderId());
        dto.setPrestoId(entity.getPreStoId());
        dto.setMemId(entity.getMemId());
        dto.setMenuId(entity.getMenuId());
        dto.setOrderStatus(entity.getOrderStatus());
        dto.setDeliveryStatus(entity.getDeliveryStatus());
        dto.setOrderTotalPrice(entity.getOrderTotalPrice());
        dto.setCustomerMessage(entity.getCustomerMessage());
        dto.setDeliveryMessage(entity.getDeliveryMessage());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setDeliveryType(entity.getDeliveryType());
        dto.setDeliveryFee(entity.getDeliveryFee());
        dto.setDiscountAmount(entity.getDiscountAmount());
        dto.setOrderCreatedAt(entity.getOrderCreatedAt());
        dto.setOrderUpdatedAt(entity.getOrderUpdatedAT());
        // userAddress와 storeAddress는 별도로 설정되거나 조회 시 매핑 필요
        dto.setUserAddress("아무것도 아니였다");
        dto.setStoreAddress("매장도 아니였습니다");

        return dto;
    }
}
