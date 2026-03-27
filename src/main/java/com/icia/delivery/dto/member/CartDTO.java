package com.icia.delivery.dto.member;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 포함한 생성자 생성
public class CartDTO {

    private Long cartId;
    private Long memId;
    private Long menuId;
    private String cartName;
    private String cartImgUrl;
    private Long cartPrice;
    private Long cartQuantity;


    public static CartDTO toDTO(CartEntity entity) {
        CartDTO dto = new CartDTO();
        dto.setCartId(entity.getCartId());
        dto.setMemId(entity.getMemId());
        dto.setMenuId(entity.getMenuId());
        dto.setCartName(entity.getCartName());
        dto.setCartPrice(entity.getCartPrice());
        dto.setCartImgUrl(entity.getCartImgUrl());
        dto.setCartQuantity(entity.getCartQuantity());

        return dto;
    }
}
