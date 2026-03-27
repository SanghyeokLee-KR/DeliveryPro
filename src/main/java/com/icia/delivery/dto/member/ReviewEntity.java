package com.icia.delivery.dto.member;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reviews")
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "pre_sto_id", nullable = false)
    private Long preStoId;

    @Column(name = "mem_id", nullable = false)
    private Long memId;

    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Column(name = "review_rating", nullable = false)
    private int reviewRating;

    @Column(name = "review_content", nullable = false, length = 1000)
    private String reviewContent;

    @Column(name = "review_image", length = 500)
    private String reviewImage;

    @Column(name = "review_created_at", nullable = false)
    private LocalDateTime reviewCreatedAt;

    @Column(name = "review_update_at")
    private LocalDateTime reviewUpdateAt;

    public static ReviewEntity toEntity(ReviewDTO dto) {
        ReviewEntity entity = new ReviewEntity();
        entity.setReviewId(dto.getReviewId());
        entity.setOrderId(dto.getOrderId());
        entity.setPreStoId(dto.getPreStoId());
        entity.setMenuId(dto.getMenuId());
        entity.setMemId(dto.getMemId());
        entity.setReviewRating(dto.getReviewRating());
        entity.setReviewContent(dto.getReviewContent());
        entity.setReviewImage(dto.getReviewImage());
        entity.setReviewCreatedAt(dto.getReviewCreatedAt());
        entity.setReviewUpdateAt(dto.getReviewUpdateAt());

        return entity;
    }
}
