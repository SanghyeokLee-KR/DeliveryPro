package com.icia.delivery.dto.member;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="cart")
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 포함한 생성자 생성
public class CartEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Oracle의 IDENTITY 컬럼에 맞게 설정
    @Column(name = "cart_id")
    private Long cartId;
    @Column(name="mem_id",  nullable = false)
    private Long memId;
    @Column(name="menu_id",  nullable = false)
    private Long menuId;
    @Column(name="cart_name")
    private String cartName;
    @Column(name="cart_img_url")
    private String cartImgUrl;
    @Column(name="cart_item_price")
    private Long cartPrice;

    @Column(name="cart_quantity")
    private Long cartQuantity;


    public static CartEntity toEntity(CartDTO dto){
        CartEntity entity = new CartEntity();
        entity.setCartId(dto.getCartId());
        entity.setMemId(dto.getMemId());
        entity.setMenuId(dto.getMenuId());
        entity.setCartName(dto.getCartName());
        entity.setCartPrice(dto.getCartPrice());
        entity.setCartImgUrl(dto.getCartImgUrl());
        entity.setCartQuantity(dto.getCartQuantity());

        return entity;
    }
}
