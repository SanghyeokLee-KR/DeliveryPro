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
public class PreStoreDTO {

    private Long preStoId;                  // 가게 PK (Primary Key, 고유 식별자)
    private Long preStoPreMemId;            // 사장님 FK -> pre_mem.pre_mem_id (사장님의 고유 식별자, pre_mem 테이블의 외래 키)
    private String preStoName;              // 가게 이름
    private String preStoCategory;          // 음식 카테고리 (예: 한식, 중식, 패스트푸드 등)
    private String preStoAddress;           // 가게 주소
    private String preStoPhoto;             // 대표 사진 (URL 또는 경로로 저장된 이미지 파일)
    private String preStoPhone;             // 매장 전화번호
    private String preStoIntro;             // 가게 소개글 (매장의 특징이나 설명)
    private Integer preStoMinOrderAmount;   // 최소 주문 금액 (배달을 시작하기 위한 최소 금액)
    private Integer preStoDeliveryFee;      // 배달 팁 (배달 서비스에 추가되는 요금)
    private Integer preStoDeliveryTimeMin;  // 최소 배달 예상 시간 (배달 예상 최소 시간, 분 단위)
    private Integer preStoDeliveryTimeMax;  // 최대 배달 예상 시간 (배달 예상 최대 시간, 분 단위)
    private Float preStoRating;             // 별점 (0~5.0 사이의 점수, 매장의 평점)
    private Integer preStoReviewCount;      // 리뷰 개수 (매장에 대한 리뷰의 총 수)
    private MultipartFile prePhoto;         // 매장 대표 사진 파일 (업로드된 사진 파일)

    private String preStoOpeningHours;      // 매장 오픈 시간 (예: "09:00-22:00")
    private String preStoOperatingDays;     // 매장 운영 요일 (예: "월요일-금요일" 또는 "매일")

    private String preStoHolidayWeek;       // 휴일 주 (매장에서 휴무인 주, 예: "매월 첫 번째 주")
    private String preStoDayOff;            // 가게 휴무일 (매주 특정 날짜나 월별 특정일 등)

    private String preStoDeliveryArea;      // 배달 가능 지역 (배달이 가능한 지역이나 구역)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime preStoCreatedAt;  // 생성일 (매장 정보가 생성된 날짜와 시간)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime preStoUpdatedAt;  // 수정일 (매장 정보가 마지막으로 수정된 날짜와 시간)

    private String preStoStatus;            // 가게 상태 (예: 승인, 차단, 폐점, 보류 등 매장의 현재 상태)

    private Long  preStoBreakStartTime;   // 가게 일시 휴식 시작 시간

    private Integer preStoBreakTime;        // 가게 일시 정지 시간



    // Entity -> DTO 변환
    public static PreStoreDTO toDTO(PreStoreEntity entity) {
        PreStoreDTO dto = new PreStoreDTO();
        dto.setPreStoId(entity.getPreStoId());
        dto.setPreStoPreMemId(entity.getPreStoPreMemId());
        dto.setPreStoName(entity.getPreStoName());
        dto.setPreStoCategory(entity.getPreStoCategory());
        dto.setPreStoAddress(entity.getPreStoAddress());
        dto.setPreStoPhoto(entity.getPreStoPhoto());
        dto.setPreStoPhone(entity.getPreStoPhone());
        dto.setPreStoIntro(entity.getPreStoIntro());
        dto.setPreStoMinOrderAmount(entity.getPreStoMinOrderAmount());
        dto.setPreStoDeliveryFee(entity.getPreStoDeliveryFee());
        dto.setPreStoDeliveryTimeMin(entity.getPreStoDeliveryTimeMin());
        dto.setPreStoDeliveryTimeMax(entity.getPreStoDeliveryTimeMax());
        dto.setPreStoRating(entity.getPreStoRating());
        dto.setPreStoReviewCount(entity.getPreStoReviewCount());
        dto.setPreStoCreatedAt(entity.getPreStoCreatedAt());
        dto.setPreStoUpdatedAt(entity.getPreStoUpdatedAt());
        dto.setPreStoStatus(entity.getPreStoStatus());
        dto.setPreStoBreakTime(entity.getPreStoBreakTime());
        dto.setPreStoBreakStartTime(entity.getPreStoBreakStartTime());
        dto.setPreStoOpeningHours(entity.getPreStoOpeningHours());
        dto.setPreStoDayOff(entity.getPreStoDayOff());
        dto.setPreStoDeliveryArea(entity.getPreStoDeliveryArea());
        dto.setPreStoOperatingDays(entity.getPreStoOperatingDays());
        dto.setPreStoHolidayWeek(entity.getPreStoHolidayWeek());
        return dto;
    }

}
