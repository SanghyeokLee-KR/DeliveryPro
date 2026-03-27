package com.icia.delivery.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 포함한 생성자 생성
public class DeliveryAddressDTO {
    private Long id; // 배송지 ID
    private Long memberId; // 회원 번호
    private String name; // 배송지 이름 (예: "집", "회사")
    private String address; // 기본 주소
    private String isMain; // 기본 배송지 여부 ("메인", "서브")
    private LocalDateTime createdDate; // 생성 일자

    /**
     * Entity -> DTO 변환
     */
    public static DeliveryAddressDTO toDTO(DeliveryAddressEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("DeliveryAddressEntity는 null일 수 없습니다.");
        }

        DeliveryAddressDTO dto = new DeliveryAddressDTO();
        dto.setId(entity.getAddrId());
        dto.setMemberId(entity.getAddrMemberId());
        dto.setName(entity.getAddrName()); // 이름 추가
        dto.setAddress(entity.getAddrAddress());
        dto.setIsMain(entity.getAddrIsMain());
        dto.setCreatedDate(entity.getAddrRegisterDate()); // 생성 일자 추가
        return dto;
    }
}
