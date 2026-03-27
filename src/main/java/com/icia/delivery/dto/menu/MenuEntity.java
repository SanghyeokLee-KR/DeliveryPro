package com.icia.delivery.dto.menu;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pre_menu")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long menuId;

    /**
     * storeId를 단순 숫자로만 매핑하고 싶다면 필드를 두고 @Column 매핑
     * 혹은 아래처럼 PreStore 엔티티를 직접 가져오는 @ManyToOne으로도 가능
     */
    @Column(name = "menu_sto_id")
    private Long storeId;

    @Column(name = "menu_category")
    private String menuCategory;

    @Column(name = "menu_name")
    private String menuName;

    @Column(name = "menu_price")
    private Integer menuPrice;

    @Column(name = "menu_picture_url")
    private String menuPictureUrl;

    @Column(name = "menu_popularity")
    private Integer menuPopularity;

    @Column(name = "menu_status")
    private String menuStatus;

    public static MenuEntity toEntity(MenuDTO dto) {
        MenuEntity entity = new MenuEntity();
        entity.setMenuId(dto.getMenuId());
        entity.setStoreId(dto.getStoreId());
        entity.setMenuCategory(dto.getMenuCategory());
        entity.setMenuName(dto.getMenuName());
        entity.setMenuPrice(dto.getMenuPrice());
        entity.setMenuPictureUrl(dto.getMenuPictureUrl());
        entity.setMenuPopularity(dto.getMenuPopularity());
        entity.setMenuStatus(dto.getMenuStatus());

        return entity;
    }
}
