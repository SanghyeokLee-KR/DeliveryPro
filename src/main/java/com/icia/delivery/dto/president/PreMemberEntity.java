package com.icia.delivery.dto.president;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pre_member")
@NoArgsConstructor
@AllArgsConstructor
public class PreMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pre_mem_id")
    private Long preMemId; // 사장님 PK

    @Column(name = "pre_mem_biz_reg_no", nullable = false, length = 30)
    private String preMemBizRegNo; // 사업자 등록 번호

    @Column(name = "pre_mem_ceo_name", nullable = false, length = 50)
    private String preMemCeoName;  // 대표자명

    @Column(name = "pre_mem_phone", length = 20)
    private String preMemPhone;    // 대표자 휴대폰 번호

    @Column(name = "pre_mem_user_id", nullable = false, length = 50)
    private String preMemUserId;   // 로그인 아이디

    @Column(name = "pre_mem_password", nullable = false, length = 200)
    private String preMemPassword; // 비밀번호 (해시 권장)

    @Column(name = "pre_mem_email", nullable = false, length = 100)
    private String preMemEmail;    // 대표자 이메일

    @Column(name = "pre_mem_biz_license_photo", length = 300)
    private String preMemBizLicensePhoto; // 사업자등록증 사진 (URL/경로)

    @Column(name = "pre_mem_created_at")
    private LocalDateTime preMemCreatedAt; // 생성일

    @Column(name = "pre_mem_updated_at")
    private LocalDateTime preMemUpdatedAt; // 수정일

    @Column(name = "pre_mem_status", length = 10)
    private String preMemStatus; // 사장님 상태 (정상, 탈퇴, 차단)

    @Column(name = "pre_mem_approval_status", length = 10)
    private String preMemApprovalStatus; // 운영자가 승인/거절 (보류, 승인, 거절)


    // DTO -> Entity 변환
    public static PreMemberEntity toEntity(PreMemberDTO dto) {
        PreMemberEntity entity = new PreMemberEntity();
        entity.setPreMemId(dto.getPreMemId());
        entity.setPreMemBizRegNo(dto.getPreMemBizRegNo());
        entity.setPreMemCeoName(dto.getPreMemCeoName());
        entity.setPreMemPhone(dto.getPreMemPhone());
        entity.setPreMemUserId(dto.getPreMemUserId());
        entity.setPreMemPassword(dto.getPreMemPassword());
        entity.setPreMemEmail(dto.getPreMemEmail());
        entity.setPreMemBizLicensePhoto(dto.getPreMemBizLicensePhoto());
        entity.setPreMemCreatedAt(dto.getPreMemCreatedAt());
        entity.setPreMemUpdatedAt(dto.getPreMemUpdatedAt());
        entity.setPreMemStatus(dto.getPreMemStatus());
        entity.setPreMemApprovalStatus(dto.getPreMemApprovalStatus());
        return entity;
    }
}
