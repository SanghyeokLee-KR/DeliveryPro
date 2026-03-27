package com.icia.delivery.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementDTO {
    private Long advId;       // PK
    private Integer advOrder; // 1~5
    private String advTitle;
    private String advImageUrl;
    private LocalDateTime advCreatedAt;
    private LocalDateTime advUpdatedAt;
}
