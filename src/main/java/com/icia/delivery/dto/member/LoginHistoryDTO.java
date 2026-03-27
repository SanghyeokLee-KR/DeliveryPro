package com.icia.delivery.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 포함한 생성자 생성
public class LoginHistoryDTO {

    private Long hisLoginId; // 로그인 고유 ID
    private Long hisMid; // 회원 ID
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime hisLoginDate; // 로그인 날짜 및 시간
    private String hisIpAddress; // IP 주소
    private String hisDeviceOs; // 기기 OS
    private String hisBrowser; // 브라우저 정보

    /**
     * Entity 객체를 DTO 객체로 변환하는 메서드
     *
     * @param entity 변환할 대상 Entity 객체
     * @return 변환된 LoginHistoryDTO 객체
     */
    public static LoginHistoryDTO toDTO(LoginHistoryEntity entity) {
        LoginHistoryDTO dto = new LoginHistoryDTO();

        dto.setHisLoginId(entity.getHisLoginId()); // 로그인 ID 설정
        dto.setHisMid(entity.getMember().getMId()); // 회원 ID 설정
        dto.setHisLoginDate(entity.getHisLoginDate()); // 로그인 날짜 설정
        dto.setHisIpAddress(entity.getHisIpAddress()); // IP 주소 설정
        dto.setHisDeviceOs(entity.getHisDeviceOs()); // 기기 OS 설정
        dto.setHisBrowser(entity.getHisBrowser()); // 브라우저 정보 설정

        return dto; // 변환된 DTO 반환
    }
}
