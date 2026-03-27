package com.icia.delivery.dto.president;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pre_store_menu")
@NoArgsConstructor
@AllArgsConstructor
public class PreStoreMenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long menuId;                    // 메뉴 PK

    @Column(name = "pre_sto_id", nullable = false)
    private Long preStoId;                  // 가게 FK

    @Column(name = "menu_category", length = 100, nullable = false)
    private String menuCategory;           // 메뉴 카테고리

    @Column(name = "menu_name", nullable = false)
    private String menuName;               // 메뉴 이름

    @Column(name = "menu_price", nullable = false)
    private Long menuPrice;               // 메뉴 가격

    @Column(name = "menu_picture_url", nullable = false)
    private String menuPictureUrl;        // 메뉴 사진 URL

    @Column(name = "menu_popularity", nullable = false)
    private Long menuPopularity;          // 인기 여부

    @Column(name = "menu_created_date")
    private LocalDateTime menuCreatedDate;       // 메뉴 생성일

    @Column(name = "menu_modified_date")
    private LocalDateTime menuModifiedDate;      // 메뉴 수정일

    @Column(name = "menu_status", length = 10)
    private String menuStatus;                   // 메뉴 상태

    @Column(name = "menu_description")
    private String menuDescription;              // 메뉴 설명


    // Entity -> DTO 변환
    public static PreStoreMenuEntity toEntity(PreStoreMenuDTO dto) {
        PreStoreMenuEntity entity = new PreStoreMenuEntity();

        entity.setMenuId(dto.getMenuId());
        entity.setPreStoId(dto.getPreStoId());
        entity.setMenuCategory(dto.getMenuCategory());
        entity.setMenuName(dto.getMenuName());
        entity.setMenuPrice(dto.getMenuPrice());
        entity.setMenuPictureUrl(dto.getMenuPictureUrl());
        entity.setMenuPopularity(dto.getMenuPopularity());
        entity.setMenuCreatedDate(dto.getMenuCreatedDate());
        entity.setMenuModifiedDate(dto.getMenuModifiedDate());
        entity.setMenuStatus(dto.getMenuStatus());
        entity.setMenuDescription(dto.getMenuDescription());

        return entity;
    }
}
