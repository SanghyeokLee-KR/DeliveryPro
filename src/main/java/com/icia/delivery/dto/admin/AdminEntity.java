package com.icia.delivery.dto.admin;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 포함한 생성자 생성
@Entity
@Table(name = "admin")
public class AdminEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId; // 운영자 ID (Primary Key)

    @Column(name = "admin_username", nullable = false, unique = true, length = 50)
    private String adminUsername; // 운영자 계정 (유니크)

    @Column(name = "admin_password", nullable = false, length = 255)
    private String adminPassword; // 운영자 비밀번호 (암호화 없이)

    @Column(name = "admin_email", nullable = false, unique = true, length = 100)
    private String adminEmail; // 운영자 이메일

    @Column(name = "admin_role", nullable = false, length = 20)
    private String adminRole = "관리자"; // 역할 (예: 관리자, 슈퍼 관리자)

    @Column(name = "admin_created_at", nullable = false)
    private LocalDateTime adminCreatedAt = LocalDateTime.now(); // 계정 생성일

    @Column(name = "admin_last_login")
    private LocalDateTime adminLastLogin; // 마지막 로그인 날짜

    @Column(name = "admin_status", nullable = false, length = 10)
    private String adminStatus = "활성"; // 계정 상태 (활성, 비활성)

    /**
     * AdminDTO 객체를 AdminEntity 객체로 변환하는 메서드
     *
     * @param dto 변환할 대상 AdminDTO 객체
     * @return 변환된 AdminEntity 객체
     */
    public static AdminEntity toEntity(AdminDTO dto) {
        AdminEntity entity = new AdminEntity();

        entity.setAdminId(dto.getAdminId()); // ID 설정
        entity.setAdminUsername(dto.getAdminUsername()); // 계정 설정
        entity.setAdminPassword(dto.getAdminPassword()); // 비밀번호 설정
        entity.setAdminEmail(dto.getAdminEmail()); // 이메일 설정
        entity.setAdminRole(dto.getAdminRole()); // 역할 설정
        entity.setAdminCreatedAt(dto.getAdminCreatedAt()); // 생성일 설정
        entity.setAdminLastLogin(dto.getAdminLastLogin()); // 마지막 로그인 설정
        entity.setAdminStatus(dto.getAdminStatus()); // 상태 설정

        return entity; // 변환된 엔티티 반환
    }
}
