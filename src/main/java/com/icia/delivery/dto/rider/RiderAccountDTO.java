package com.icia.delivery.dto.rider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 포함한 생성자 생성
@Builder // 빌더 패턴을 사용할 수 있도록 설정
public class RiderAccountDTO {

    private Long riderAid;        // 계좌 PK 아이디
    private Long riderNo;         // 라이더(PK) 번호 - FK
    private String riderBankName;      // 은행명
    private String riderAccountNumber; // 계좌번호
    private String riderAccountHolder; // 예금주명

    // Entity -> DTO 변환
    public static RiderAccountDTO toDTO(RiderAccountEntity entity){
        RiderAccountDTO dto = new RiderAccountDTO();

        dto.setRiderAid(entity.getRiderAid());
        dto.setRiderNo(entity.getRiderNo());
        dto.setRiderBankName(entity.getRiderBankName());
        dto.setRiderAccountNumber(entity.getRiderAccountNumber());
        dto.setRiderAccountHolder(entity.getRiderAccountHolder());

        return dto;
    }
}
