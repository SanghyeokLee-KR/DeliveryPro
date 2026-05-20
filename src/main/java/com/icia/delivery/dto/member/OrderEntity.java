package com.icia.delivery.dto.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "pre_sto_id", nullable = false)
    private Long preStoId;

    @Column(name = "mem_id", nullable = false)
    private Long memId;

    @Column(name = "menu_Id", nullable = false)
    private Long menuId;

    @Column(name = "order_status", columnDefinition = "NVARCHAR2(20) DEFAULT '주문접수'")
    private String orderStatus;

    @Column(name = "order_total_price")
    private double orderTotalPrice;

    @Column(name = "customer_message" ,length = 500)
    private String customerMessage;

    @Column(name = "delivery_message" ,length = 500)
    private String deliveryMessage;
    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "delivery_type")
    private String deliveryType;

    @Column(name="delivery_status")
    private String deliveryStatus;

    @Column(name="delivery_fee")
    private int deliveryFee;

    @Column(name = "order_created_at" , columnDefinition = "TIMESTAMP DEFAULT TRUNC(SYSDATE)")
    private LocalDateTime orderCreatedAt;

    @Column(name = "order_updated_at")
    private LocalDateTime orderUpdatedAT;

    @Column(name = "discount_amount")
    private int discountAmount;

    public static OrderEntity toEntity(OrderDTO dto) {
        OrderEntity entity = new OrderEntity();

        entity.setOrderId(dto.getOrderId());
        entity.setPreStoId(dto.getPrestoId());
        entity.setMemId(dto.getMemId());
        entity.setMenuId(dto.getMenuId());
        entity.setOrderStatus(dto.getOrderStatus());
        entity.setOrderTotalPrice(dto.getOrderTotalPrice());
        entity.setCustomerMessage(dto.getCustomerMessage());
        entity.setDeliveryMessage(dto.getDeliveryMessage());
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setDeliveryType(dto.getDeliveryType());
        entity.setDeliveryFee(dto.getDeliveryFee());
        entity.setDeliveryStatus(dto.getDeliveryStatus());
        entity.setOrderCreatedAt(dto.getOrderCreatedAt());
        entity.setOrderUpdatedAT(dto.getOrderUpdatedAt());
        entity.setDiscountAmount(dto.getDiscountAmount());

        return entity;
    }
}
