// src/main/java/com/icia/delivery/dto/member/MemberDTO.java
package com.icia.delivery.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 포함한 생성자 생성
@Builder // 빌더 패턴을 사용할 수 있도록 설정
public class MemberDTO {
    private Long mId; // 회원 고유 ID
    private String userId; // 회원 아이디 (로그인 시 사용)
    private String email; // 회원 이메일 주소
    private String password; // 회원 비밀번호 (암호화된 값 저장)
    private String username; // 회원 실명 (법적 이름)
    private String nickname; // 회원 닉네임 (별명)
    private String phone; // 회원 휴대전화 번호

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday; // 회원 생년월일

    private String gender; // 성별 (남성, 여성)
    private Long point; // 회원 포인트 (적립금)
    private String grade; // 회원 등급 (Welcome, Family, VIP, VVIP)
    private String address; // 회원 주소
    private String registerIp; // 회원 가입 시 사용한 IP 주소
    private String lastLoginIp; // 최종 로그인 시 사용한 IP 주소

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerDate; // 회원 가입 날짜 및 시간

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginDate; // 최종 로그인 날짜 및 시간

    private String status; // 회원 상태 (활성, 정지, 탈퇴)
    private String receiveEmail; // 이메일 수신 여부 (수신, 거부)
    private String openProfile; // 개인정보 공개 여부 (공개, 비공개)
    private String receiveNotify; // 알림 수신 여부 (수신, 거부)
    private String loginType; // 로그인 유형 (NAVER, KAKAO, GOOGLE, LOCAL)

    // 포맷된 날짜 추가
    private String formattedRegisterDate;
    private String formattedLastLoginDate;

    /**
     * MemberEntity 객체를 MemberDTO로 변환하는 메서드
     *
     * @param entity 변환 대상 MemberEntity 객체
     * @return 변환된 MemberDTO 객체
     */
    public static MemberDTO toDTO(MemberEntity entity) {
        MemberDTO dto = new MemberDTO();

        dto.setMId(entity.getMId());
        dto.setUserId(entity.getUserId());
        dto.setEmail(entity.getEmail());
        dto.setPassword(entity.getPassword());
        dto.setUsername(entity.getUsername());
        dto.setNickname(entity.getNickname());
        dto.setPhone(entity.getPhone());
        dto.setBirthday(entity.getBirthday());
        dto.setGender(entity.getGender());
        dto.setPoint(entity.getPoint());
        dto.setGrade(entity.getGrade());
        dto.setAddress(entity.getAddress());
        dto.setRegisterIp(entity.getRegisterIp());
        dto.setLastLoginIp(entity.getLastLoginIp());
        dto.setRegisterDate(entity.getRegisterDate());
        dto.setLastLoginDate(entity.getLastLoginDate());
        dto.setStatus(entity.getStatus());
        dto.setReceiveEmail(entity.getReceiveEmail());
        dto.setOpenProfile(entity.getOpenProfile());
        dto.setReceiveNotify(entity.getReceiveNotify());
        dto.setLoginType(entity.getLoginType());

        // 날짜 포맷팅
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        if (entity.getRegisterDate() != null) {
            dto.setFormattedRegisterDate(entity.getRegisterDate().format(formatter));
        }
        if (entity.getLastLoginDate() != null) {
            dto.setFormattedLastLoginDate(entity.getLastLoginDate().format(formatter));
        }

        return dto;
    }
}
