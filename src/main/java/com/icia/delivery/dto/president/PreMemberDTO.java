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
public class PreMemberDTO {

    private Long preMemId;                // PK
    private String preMemBizRegNo;        // 사업자 등록 번호
    private String preMemCeoName;         // 대표자명
    private String preMemPhone;           // 대표자 휴대폰 번호
    private String preMemUserId;          // 로그인 아이디
    private String preMemPassword;        // 비밀번호
    private String preMemEmail;           // 대표자 이메일
    private String preMemBizLicensePhoto; // 사업자등록증 사진 (URL/경로)
    private MultipartFile pFile;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime preMemCreatedAt; // 생성일

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime preMemUpdatedAt; // 수정일

    private String preMemStatus;           // 사장님 상태 (정상, 탈퇴, 차단)
    private String preMemApprovalStatus;   // 운영자 승인/거절 (보류, 승인, 거절)


    // Entity -> DTO 변환
    public static PreMemberDTO toDTO(PreMemberEntity entity) {
        PreMemberDTO dto = new PreMemberDTO();
        dto.setPreMemId(entity.getPreMemId());
        dto.setPreMemBizRegNo(entity.getPreMemBizRegNo());
        dto.setPreMemCeoName(entity.getPreMemCeoName());
        dto.setPreMemPhone(entity.getPreMemPhone());
        dto.setPreMemUserId(entity.getPreMemUserId());
        dto.setPreMemPassword(entity.getPreMemPassword());
        dto.setPreMemEmail(entity.getPreMemEmail());
        dto.setPreMemBizLicensePhoto(entity.getPreMemBizLicensePhoto());
        dto.setPreMemCreatedAt(entity.getPreMemCreatedAt());
        dto.setPreMemUpdatedAt(entity.getPreMemUpdatedAt());
        dto.setPreMemStatus(entity.getPreMemStatus());
        dto.setPreMemApprovalStatus(entity.getPreMemApprovalStatus());
        return dto;
    }
}
