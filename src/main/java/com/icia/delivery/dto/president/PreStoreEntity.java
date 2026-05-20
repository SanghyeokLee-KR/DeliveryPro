package com.icia.delivery.dto.president;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pre_store")
@NoArgsConstructor
@AllArgsConstructor
public class PreStoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pre_sto_id")
    private Long preStoId; // 가게 PK

    @Column(name = "pre_sto_pre_mem_id", nullable = false)
    private Long preStoPreMemId; // 사장님 FK -> pre_mem.pre_mem_id

    @Column(name = "pre_sto_name", nullable = false, length = 100)
    private String preStoName; // 가게 이름

    @Column(name = "pre_sto_category", length = 50)
    private String preStoCategory; // 음식 카테고리

    @Column(name = "pre_sto_address", length = 200)
    private String preStoAddress; // 주소

    @Column(name = "pre_sto_photo", length = 300)
    private String preStoPhoto; // 대표 사진 (URL/경로)

    @Column(name = "pre_sto_phone", length = 20)
    private String preStoPhone; // 매장 전화번호

    @Column(name = "pre_sto_intro", length = 1000)
    private String preStoIntro; // 가게 소개글

    @Column(name = "pre_sto_min_order_amount")
    private Integer preStoMinOrderAmount; // 최소 주문 금액

    @Column(name = "pre_sto_delivery_fee")
    private Integer preStoDeliveryFee; // 배달 팁

    @Column(name = "pre_sto_delivery_time_min")
    private Integer preStoDeliveryTimeMin; // 최소 배달 예상 시간(분)

    @Column(name = "pre_sto_delivery_time_max")
    private Integer preStoDeliveryTimeMax; // 최대 배달 예상 시간(분)

    @Column(name = "pre_sto_rating", nullable = false)
    private Float preStoRating; // 별점(0~5.0)

    @Column(name = "pre_sto_review_count", nullable = false)
    private Integer preStoReviewCount; // 리뷰 개수

    // 날짜 타입을 LocalDateTime으로 매핑
    @Column(name = "pre_sto_created_at")
    private LocalDateTime preStoCreatedAt; // 생성일

    @Column(name = "pre_sto_updated_at")
    private LocalDateTime preStoUpdatedAt; // 수정일

    @Column(name = "pre_sto_status", length = 10)
    private String preStoStatus; // 가게 상태 (승인, 차단, 폐점, 보류)

    @Column(name = "pre_sto_break_start_time")
    private Long preStoBreakStartTime;

    @Column(name = "pre_sto_break_time")
    private Integer preStoBreakTime;        // 가게 일시 정지 시간

    @Column(name = "pre_sto_opening_hours", length = 100)
    private String preStoOpeningHours;

    @Column(name = "pre_sto_day_off", length = 100)
    private String preStoDayOff;

    @Column(name = "pre_sto_delivery_area", length = 200)
    private String preStoDeliveryArea;

    @Column(name = "pre_sto_operating_days", length = 100)
    private String preStoOperatingDays;

    @Column(name = "pre_sto_holi_day_week", length = 100)
    private String preStoHolidayWeek;


    // DTO -> Entity 변환
    public static PreStoreEntity toEntity(PreStoreDTO dto) {
        PreStoreEntity entity = new PreStoreEntity();
        entity.setPreStoId(dto.getPreStoId());
        entity.setPreStoPreMemId(dto.getPreStoPreMemId());
        entity.setPreStoName(dto.getPreStoName());
        entity.setPreStoCategory(dto.getPreStoCategory());
        entity.setPreStoAddress(dto.getPreStoAddress());
        entity.setPreStoPhoto(dto.getPreStoPhoto());
        entity.setPreStoPhone(dto.getPreStoPhone());
        entity.setPreStoIntro(dto.getPreStoIntro());
        entity.setPreStoMinOrderAmount(dto.getPreStoMinOrderAmount());
        entity.setPreStoDeliveryFee(dto.getPreStoDeliveryFee());
        entity.setPreStoDeliveryTimeMin(dto.getPreStoDeliveryTimeMin());
        entity.setPreStoDeliveryTimeMax(dto.getPreStoDeliveryTimeMax());
        entity.setPreStoRating(dto.getPreStoRating());
        entity.setPreStoReviewCount(dto.getPreStoReviewCount());
        entity.setPreStoCreatedAt(dto.getPreStoCreatedAt());
        entity.setPreStoUpdatedAt(dto.getPreStoUpdatedAt());
        entity.setPreStoStatus(dto.getPreStoStatus());
        entity.setPreStoBreakStartTime(dto.getPreStoBreakStartTime());
        entity.setPreStoBreakTime(dto.getPreStoBreakTime());
        entity.setPreStoOpeningHours(dto.getPreStoOpeningHours());
        entity.setPreStoDayOff(dto.getPreStoDayOff());
        entity.setPreStoDeliveryArea(dto.getPreStoDeliveryArea());
        entity.setPreStoOperatingDays(dto.getPreStoOperatingDays());
        entity.setPreStoHolidayWeek(dto.getPreStoHolidayWeek());
        return entity;
    }
}
