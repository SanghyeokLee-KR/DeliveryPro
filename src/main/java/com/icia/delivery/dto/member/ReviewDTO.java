package com.icia.delivery.dto.member;


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
public class ReviewDTO {

    private Long reviewId;
    private Long orderId;
    private Long preStoId;
    private Long memId;
    private Long menuId;
    private int reviewRating;
    private String reviewContent;
    private String reviewImage;
    private LocalDateTime reviewCreatedAt;
    private LocalDateTime reviewUpdateAt;
    private MultipartFile reviewPicture;

    public static ReviewDTO toDTO(ReviewEntity entity) {
        ReviewDTO dto = new ReviewDTO();
        dto.setReviewId(entity.getReviewId());
        dto.setOrderId(entity.getOrderId());
        dto.setPreStoId(entity.getPreStoId());
        dto.setMemId(entity.getMemId());
        dto.setMenuId(entity.getMenuId());
        dto.setReviewRating(entity.getReviewRating());
        dto.setReviewContent(entity.getReviewContent());
        dto.setReviewImage(entity.getReviewImage());
        dto.setReviewCreatedAt(entity.getReviewCreatedAt());
        dto.setReviewUpdateAt(entity.getReviewUpdateAt());
      return dto;
    }
}
