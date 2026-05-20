package com.icia.delivery.dto.rider;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "riders")
@NoArgsConstructor
@AllArgsConstructor
public class RiderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rider_No")
    private Long riderNo;         // 라이더 고유 번호 (PK)

    @Column(name = "rider_id", nullable = false, length = 50, unique = true)
    private String riderId;       // 라이더 아이디

    @Column(name = "rider_pw", length = 100)
    private String riderPw;       // 라이더 비밀번호

    @Column(name = "rider_name", length = 50)
    private String riderName;     // 라이더 이름

    @Column(name = "rider_phone", length = 20)
    private String riderPhone;    // 라이더 전화번호

    @Column(name = "vehicle_type", length = 20)
    private String vehicleType;   // 라이더 차량 종류 (예: 오토바이, 자전거 등)

    @Column(name = "rider_gender", length = 20)
    private String riderGender;   // 라이더 성별

    @Temporal(TemporalType.DATE)
    @Column(name = "rider_birth")
    private String riderBirth;    // 라이더 생년월일

    // 날짜 타입을 LocalDateTime으로 매핑
    @Column(name = "rider_created_at")
    private LocalDateTime riderCreatedAt; // 생성일

    @Column(name = "total_deliveries")
    private Integer totalDeliveries; // 총 배달 횟수

    @Column(name = "is_available", length = 10, nullable = false)
    private String isAvailable;   // 라이더의 현재 상태 (배달 허용 여부 일듯)

    // Entity -> DTO 변환
    public static RiderEntity toEntity(RiderDTO dto){
        RiderEntity entity = new RiderEntity();

        entity.setRiderNo(dto.getRiderNo());
        entity.setRiderId(dto.getRiderId());
        entity.setRiderPw(dto.getRiderPw());
        entity.setRiderName(dto.getRiderName());
        entity.setRiderPhone(dto.getRiderPhone());
        entity.setVehicleType(dto.getVehicleType());
        entity.setRiderGender(dto.getRiderGender());
        entity.setRiderBirth(dto.getRiderBirth());
        entity.setRiderCreatedAt(dto.getRiderCreatedAt());
        entity.setTotalDeliveries(dto.getTotalDeliveries());
        entity.setIsAvailable(dto.getIsAvailable());

        return entity;
    }

}
