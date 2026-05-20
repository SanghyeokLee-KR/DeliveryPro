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
@Table(name = "orderitem")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;


    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "menu_id" , nullable = false)
    private Long menuId;
    @Column(name = "item_name" , nullable = false)
    private String itemName;
    @Column(name = "quantity" , nullable = false)
    private Long Quantity;

    @Column(name = "item_price" , nullable = false)
    private double itemPrice;
    @Column(name = "total_price" , nullable = false)
    private double totalPrice;


    @Column(name = "order_date" , columnDefinition = "TIMESTAMP DEFAULT TRUNC(SYSDATE)" )
    private LocalDateTime orderDate;



    public static OrderItemEntity toEntity(OrderItemDTO dto) {
        OrderItemEntity entity = new OrderItemEntity();

        entity.setOrderItemId(dto.getOrderItemId());
        entity.setMenuId(dto.getMenuId());
        entity.setItemName(dto.getItemName());
        entity.setQuantity(dto.getQuantity());
        entity.setItemPrice(dto.getItemPrice());
        entity.setOrderId(dto.getOrderId());
        entity.setTotalPrice(dto.getTotalPrice());
        entity.setOrderDate(dto.getOrderDate());

        return entity;
    }


}
