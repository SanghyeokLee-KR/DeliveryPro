package com.icia.delivery.dto.president;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pre_store_photo")
@NoArgsConstructor
@AllArgsConstructor
public class PreStorePhotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pre_sto_photo_id")
    private Long preStoPhotoId; // 가게 사진 PK

    @Column(name = "pre_sto_photo_store_id", nullable = false)
    private Long preStoPhotoStoreId; // 가게 FK -> pre_store.pre_sto_id

    @Column(name = "pre_sto_photo_url", nullable = false, length = 300)
    private String preStoPhotoUrl; // 사진 경로/URL

    @Column(name = "pre_sto_photo_created_at")
    private LocalDateTime preStoPhotoCreatedAt; // 등록일

    @Column(name = "pre_sto_photo_updated_at")
    private LocalDateTime preStoPhotoUpdatedAt; // 수정일


    // DTO -> Entity 변환
    public static PreStorePhotoEntity toEntity(PreStorePhotoDTO dto) {
        PreStorePhotoEntity entity = new PreStorePhotoEntity();
        entity.setPreStoPhotoId(dto.getPreStoPhotoId());
        entity.setPreStoPhotoStoreId(dto.getPreStoPhotoStoreId());
        entity.setPreStoPhotoUrl(dto.getPreStoPhotoUrl());
        entity.setPreStoPhotoCreatedAt(dto.getPreStoPhotoCreatedAt());
        entity.setPreStoPhotoUpdatedAt(dto.getPreStoPhotoUpdatedAt());
        return entity;
    }
}
