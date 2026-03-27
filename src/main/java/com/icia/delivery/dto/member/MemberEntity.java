package com.icia.delivery.dto.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "member")
@NoArgsConstructor
@AllArgsConstructor
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mem_id")
    private Long mId; // 회원 고유 ID (기본키)

    @Column(name = "mem_userid", nullable = false, length = 100, unique = true)
    private String userId; // 회원 아이디 (유일값)

    @Column(name = "mem_email", nullable = false, length = 255)
    private String email; // 회원 이메일 주소

    @Column(name = "mem_password", length = 255)
    private String password; // 회원 비밀번호

    @Column(name = "mem_username", nullable = false, length = 100)
    private String username; // 회원 실명 (법적 이름)

    @Column(name = "mem_nickname", length = 100)
    private String nickname; // 회원 닉네임

    @Column(name = "mem_phone", length = 20)
    private String phone; // 회원 휴대전화 번호

    @Temporal(TemporalType.DATE)
    @Column(name = "mem_birthday")
    private LocalDate birthday; // 회원 생년월일

    @Column(name = "mem_gender", length = 10)
    private String gender; // 성별 (남성, 여성)

    @Column(name = "mem_point", precision = 10, scale = 0, nullable = false)
    private Long point = 0L; // 회원 포인트 (기본값: 0)

    @Column(name = "mem_grade", nullable = false, length = 10)
    private String grade; // 회원 등급

    @Column(name = "mem_address", length = 255)
    private String address; // 회원 주소

    @Column(name = "mem_register_ip", length = 50, nullable = false)
    private String registerIp; // 회원 가입 시 사용한 IP 주소

    @Column(name = "mem_lastlogin_ip", length = 50)
    private String lastLoginIp; // 최종 로그인 시 사용한 IP 주소

    @Column(name = "mem_register_date", columnDefinition = "TIMESTAMP DEFAULT TRUNC(SYSDATE)")
    private LocalDateTime registerDate; // 회원 가입 일자

    @Column(name = "mem_lastlogin_date", columnDefinition = "TIMESTAMP DEFAULT TRUNC(SYSDATE)")
    private LocalDateTime lastLoginDate; // 회원 최종 로그인 일자

    @Column(name = "mem_status", length = 10, nullable = false)
    private String status; // 회원 상태 (활성, 정지, 탈퇴)

    @Column(name = "mem_receive_email", length = 10, nullable = false)
    private String receiveEmail; // 이메일 수신 여부 (승인, 거절)

    @Column(name = "mem_open_profile", length = 10, nullable = false)
    private String openProfile; // 개인정보 공개 여부 (승인, 거절)

    @Column(name = "mem_receive_notify", length = 10, nullable = false)
    private String receiveNotify; // 알림 수신 여부 (승인, 거절)

    @Column(name = "mem_login_type", length = 20)
    private String loginType; // 로그인 방식

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<LoginHistoryEntity> loginHistories;

    /**
     * MemberDTO 객체를 MemberEntity 객체로 변환하는 메서드
     *
     * @param dto 변환할 대상 DTO 객체
     * @return 변환된 MemberEntity 객체
     */
    public static MemberEntity toEntity(MemberDTO dto) {
        MemberEntity entity = new MemberEntity();

        entity.setMId(dto.getMId()); // ID 설정
        entity.setUserId(dto.getUserId()); // 아이디 설정
        entity.setEmail(dto.getEmail()); // 이메일 설정
        entity.setPassword(dto.getPassword()); // 비밀번호 설정
        entity.setUsername(dto.getUsername()); // 이름 설정
        entity.setNickname(dto.getNickname()); // 닉네임 설정
        entity.setPhone(dto.getPhone()); // 전화번호 설정
        entity.setBirthday(dto.getBirthday()); // 생년월일 설정
        entity.setGender(dto.getGender()); // 성별 설정
        entity.setPoint(dto.getPoint()); // 포인트 설정
        entity.setGrade(dto.getGrade()); // 등급 설정
        entity.setAddress(dto.getAddress()); // 주소 설정
        entity.setRegisterIp(dto.getRegisterIp()); // 가입 IP 설정
        entity.setLastLoginIp(dto.getLastLoginIp()); // 최종 로그인 IP 설정
        entity.setRegisterDate(dto.getRegisterDate()); // 가입 일자 설정
        entity.setLastLoginDate(dto.getLastLoginDate()); // 최종 로그인 일자 설정
        entity.setStatus(dto.getStatus()); // 상태 설정
        entity.setReceiveEmail(dto.getReceiveEmail()); // 이메일 수신 여부 설정
        entity.setOpenProfile(dto.getOpenProfile()); // 프로필 공개 여부 설정
        entity.setReceiveNotify(dto.getReceiveNotify()); // 알림 수신 여부 설정
        entity.setLoginType(dto.getLoginType()); // 로그인 방식 설정

        return entity; // 변환된 엔티티 반환
    }
}
