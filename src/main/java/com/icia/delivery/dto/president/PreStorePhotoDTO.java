package com.icia.delivery.dto.president;

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
public class PreStorePhotoDTO {

    private Long preStoPhotoId;       // 가게 사진 PK
    private Long preStoPhotoStoreId;  // 가게 FK -> pre_store.pre_sto_id
    private String preStoPhotoUrl;    // 사진 경로/URL

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime preStoPhotoCreatedAt; // 등록일

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime preStoPhotoUpdatedAt; // 수정일


    // Entity -> DTO 변환
    public static PreStorePhotoDTO toDTO(PreStorePhotoEntity entity) {
        PreStorePhotoDTO dto = new PreStorePhotoDTO();
        dto.setPreStoPhotoId(entity.getPreStoPhotoId());
        dto.setPreStoPhotoStoreId(entity.getPreStoPhotoStoreId());
        dto.setPreStoPhotoUrl(entity.getPreStoPhotoUrl());
        dto.setPreStoPhotoCreatedAt(entity.getPreStoPhotoCreatedAt());
        dto.setPreStoPhotoUpdatedAt(entity.getPreStoPhotoUpdatedAt());
        return dto;
    }
}
