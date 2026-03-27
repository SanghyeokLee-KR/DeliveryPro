package com.icia.delivery.dto.rider;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 포함한 생성자 생성
@Builder // 빌더 패턴을 사용할 수 있도록 설정
public class RiderDTO {

    private Long riderNo;         // 라이더 고유 번호 (PK)
    private String riderId;       // 라이더 아이디
    private String riderPw;       // 라이더 비밀번호
    private String riderName;     // 라이더 이름
    private String riderPhone;    // 라이더 전화번호
    private String vehicleType;   // 라이더 차량 종류 (예: 오토바이, 자전거 등)
    private String riderGender;   // 라이더 성별

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String riderBirth;    // 라이더 생년월일

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime riderCreatedAt;  // 생성일

    private Integer totalDeliveries; // 총 배달 횟수
    private String isAvailable;   // 라이더의 현재 상태 (배달 허용 여부 일듯)

    // Entity -> DTO 변환
    public static RiderDTO toDTO(RiderEntity entity){
        RiderDTO dto = new RiderDTO();

        dto.setRiderNo(entity.getRiderNo());
        dto.setRiderId(entity.getRiderId());
        dto.setRiderPw(entity.getRiderPw());
        dto.setRiderName(entity.getRiderName());
        dto.setRiderPhone(entity.getRiderPhone());
        dto.setVehicleType(entity.getVehicleType());
        dto.setRiderGender(entity.getRiderGender());
        dto.setRiderBirth(entity.getRiderBirth());
        dto.setRiderCreatedAt(entity.getRiderCreatedAt());
        dto.setTotalDeliveries(entity.getTotalDeliveries());
        dto.setIsAvailable(entity.getIsAvailable());

        return dto;
    }

}
