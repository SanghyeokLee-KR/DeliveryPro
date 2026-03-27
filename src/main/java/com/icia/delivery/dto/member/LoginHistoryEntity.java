package com.icia.delivery.dto.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "login_history")
@NoArgsConstructor
@AllArgsConstructor
public class LoginHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "his_login_id")
    private Long hisLoginId; // 로그인 고유 ID (기본키)

    @ManyToOne(fetch = FetchType.LAZY) // 회원 엔티티와 다대일 관계 설정
    @JoinColumn(name = "his_mid", nullable = false)
    private MemberEntity member; // 회원 엔티티 참조

    @Column(name = "his_login_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT TRUNC(SYSDATE)")
    private LocalDateTime hisLoginDate; // 로그인 날짜 및 시간 (밀리초 제거)

    @Column(name = "his_ip_address", length = 45)
    private String hisIpAddress; // IP 주소

    @Column(name = "his_device_os", length = 50)
    private String hisDeviceOs; // 기기 OS

    @Column(name = "his_browser", length = 50)
    private String hisBrowser; // 브라우저 정보

    /**
     * DTO 객체를 LoginHistoryEntity 객체로 변환하는 메서드
     *
     * @param dto    변환할 대상 DTO 객체
     * @param member 회원 엔티티
     * @return 변환된 LoginHistoryEntity 객체
     */
    public static LoginHistoryEntity toEntity(LoginHistoryDTO dto, MemberEntity member) {
        LoginHistoryEntity entity = new LoginHistoryEntity();

        entity.setHisLoginId(dto.getHisLoginId()); // 로그인 ID 설정
        entity.setMember(member); // 회원 엔티티 참조 설정
        entity.setHisLoginDate(dto.getHisLoginDate()); // 로그인 날짜 설정
        entity.setHisIpAddress(dto.getHisIpAddress()); // IP 주소 설정
        entity.setHisDeviceOs(dto.getHisDeviceOs()); // 기기 OS 설정
        entity.setHisBrowser(dto.getHisBrowser()); // 브라우저 정보 설정

        return entity; // 변환된 엔티티 반환
    }
}
