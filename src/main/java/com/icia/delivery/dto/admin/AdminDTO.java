package com.icia.delivery.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 포함한 생성자 생성
public class AdminDTO {
    private Long adminId; // 운영자 고유 ID
    private String adminUsername; // 운영자 계정
    private String adminPassword; // 운영자 비밀번호
    private String adminEmail; // 운영자 이메일
    private String adminRole; // 역할
    private LocalDateTime adminCreatedAt; // 계정 생성일
    private LocalDateTime adminLastLogin; // 마지막 로그인 날짜
    private String adminStatus; // 계정 상태

    /**
     * AdminEntity 객체를 AdminDTO로 변환하는 메서드
     *
     * @param entity 변환 대상 AdminEntity 객체
     * @return 변환된 AdminDTO 객체
     */
    public static AdminDTO toDTO(AdminEntity entity) {
        AdminDTO dto = new AdminDTO();

        dto.setAdminId(entity.getAdminId()); // ID 설정
        dto.setAdminUsername(entity.getAdminUsername()); // 계정 설정
        dto.setAdminPassword(entity.getAdminPassword()); // 비밀번호 설정
        dto.setAdminEmail(entity.getAdminEmail()); // 이메일 설정
        dto.setAdminRole(entity.getAdminRole()); // 역할 설정
        dto.setAdminCreatedAt(entity.getAdminCreatedAt()); // 생성일 설정
        dto.setAdminLastLogin(entity.getAdminLastLogin()); // 마지막 로그인 설정
        dto.setAdminStatus(entity.getAdminStatus()); // 상태 설정

        return dto; // 변환된 DTO 반환
    }
}
