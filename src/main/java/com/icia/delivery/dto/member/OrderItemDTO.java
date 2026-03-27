package com.icia.delivery.dto.member;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {


    private Long orderItemId;
    private Long orderId;
    private Long menuId;
    private String itemName;
    private Long quantity;
    private double itemPrice;
    private double totalPrice;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderDate;


    public static OrderItemDTO toDTO(OrderItemEntity entity){
        OrderItemDTO dto = new OrderItemDTO();

        dto.setOrderItemId(entity.getOrderItemId());
        dto.setOrderId(entity.getOrderId());
        dto.setMenuId(entity.getMenuId());
        dto.setItemName(entity.getItemName());
        dto.setQuantity(entity.getQuantity());
        dto.setItemPrice(entity.getItemPrice());
        dto.setTotalPrice(entity.getTotalPrice());
        dto.setOrderDate(entity.getOrderDate());
        return dto;
    }


}
