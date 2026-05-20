package com.icia.delivery.dto.menu;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuDTO {
    private Long menuId;
    private Long storeId;
    private String menuCategory;
    private String menuName;
    private Integer menuPrice;
    private String menuPictureUrl;
    private Integer menuPopularity; // 1: 인기, 0: 일반
    private String menuStatus; // 예: "일반", "품절" 등

    public static MenuDTO toDTO(MenuEntity entity) {
        MenuDTO dto = new MenuDTO();
        dto.setMenuId(entity.getMenuId());
        dto.setStoreId(entity.getStoreId());
        dto.setMenuCategory(entity.getMenuCategory());
        dto.setMenuName(entity.getMenuName());
        dto.setMenuPrice(entity.getMenuPrice());
        dto.setMenuPictureUrl(entity.getMenuPictureUrl());
        dto.setMenuPopularity(entity.getMenuPopularity());
        dto.setMenuStatus(entity.getMenuStatus());
        return dto;
    }
}