package com.icia.delivery.dto.president;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreStoreMenuDTO {

    private Long menuId;                 // 메뉴 PK
    private Long preStoId;               // 가게 FK
    private String menuCategory;         // 메뉴 카테고리
    private String menuName;             // 메뉴 이름
    private Long menuPrice;              // 메뉴 가격
    private String menuPictureUrl;       // 메뉴 사진 URL
    private Long menuPopularity;         // 인기 여부
    private MultipartFile mpFile;
    private String menuDescription;      // 메뉴 설명

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime menuCreatedDate;   // 메뉴 생성일

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime menuModifiedDate;  // 메뉴 수정일

    private String menuStatus;               // 메뉴 상태

    // Entity -> DTO 변환
    public static PreStoreMenuDTO toDTO(PreStoreMenuEntity entity) {
        PreStoreMenuDTO dto = new PreStoreMenuDTO();

        dto.setMenuId(entity.getMenuId());
        dto.setPreStoId(entity.getPreStoId());
        dto.setMenuCategory(entity.getMenuCategory());
        dto.setMenuName(entity.getMenuName());
        dto.setMenuPrice(entity.getMenuPrice());
        dto.setMenuPictureUrl(entity.getMenuPictureUrl());
        dto.setMenuPopularity(entity.getMenuPopularity());
        dto.setMenuCreatedDate(entity.getMenuCreatedDate());
        dto.setMenuModifiedDate(entity.getMenuModifiedDate());
        dto.setMenuStatus(entity.getMenuStatus());
        dto.setMenuDescription(entity.getMenuDescription());

        return dto;
    }
}
