package com.icia.delivery.dto.admin;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "advertisements")
public class AdvertisementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adv_id")
    private Long advId;  // PK (자동 증가)

    @Column(name = "adv_order", nullable = false, unique = true)
    private Integer advOrder; // 1~5 중 하나

    @Column(name = "adv_title", length = 100)
    private String advTitle;

    @Column(name = "adv_image_url", length = 255)
    private String advImageUrl;

    @Column(name = "adv_created_at", nullable = false)
    private LocalDateTime advCreatedAt = LocalDateTime.now();

    @Column(name = "adv_updated_at", nullable = false)
    private LocalDateTime advUpdatedAt = LocalDateTime.now();
}
