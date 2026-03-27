// src/main/java/com/icia/delivery/service/admin/OperatorService.java
package com.icia.delivery.service.admin;

import com.icia.delivery.dao.member.LoginHistoryRepository;
import com.icia.delivery.dao.member.MemberRepository;
import com.icia.delivery.dto.member.LoginHistoryDTO;
import com.icia.delivery.dto.member.LoginHistoryEntity;
import com.icia.delivery.dto.member.MemberDTO;
import com.icia.delivery.dto.member.MemberEntity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperatorService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    // 회원 ID로 userId만 조회
    public List<String> getMemberUserIdById(Long memberId) {
        return memberRepository.findMemberUserIdById(memberId);
    }


    /**
     * 모든 회원을 페이징하여 조회하는 메서드
     *
     * @param pageable 페이징 정보
     * @return 페이징된 회원 리스트 DTO
     */
    public Page<MemberDTO> getAllMembers(Pageable pageable) {
        Page<MemberEntity> memberEntities = memberRepository.findAll(pageable);
        return memberEntities.map(MemberDTO::toDTO);
    }

    /**
     * 검색어와 필터링 조건을 기반으로 회원을 조회하는 메서드
     *
     * @param searchQuery 회원 아이디 또는 이름 검색어
     * @param gender      성별 필터
     * @param grade       등급 필터
     * @param status      상태 필터
     * @param pageable    페이징 및 정렬 정보
     * @return 페이징된 필터링된 회원 리스트 DTO
     */
    public Page<MemberDTO> searchMembers(String searchQuery, String gender, String grade, String status, Pageable pageable) {
        Page<MemberEntity> memberEntities = memberRepository.searchMembers(searchQuery, gender, grade, status, pageable);
        return memberEntities.map(MemberDTO::toDTO);
    }

    /**
     * 특정 ID를 가진 회원을 조회하는 메서드
     *
     * @param id 회원 ID
     * @return 회원 DTO 또는 null
     */
    public MemberDTO getMemberById(Long id) {
        return memberRepository.findById(id)
                .map(MemberDTO::toDTO)
                .orElse(null);
    }

    /**
     * 회원의 정보를 업데이트하는 메서드
     *
     * @param id         회원 ID
     * @param memberForm 수정된 회원 정보가 담긴 MemberDTO
     * @return 업데이트 성공 여부
     */
    public boolean updateMemberInfo(Long id, MemberDTO memberForm) {
        return memberRepository.findById(id).map(member -> {
            // 수정 가능한 필드 업데이트
            member.setUsername(memberForm.getUsername());
            member.setPhone(memberForm.getPhone());
            member.setGender(memberForm.getGender());
            member.setGrade(memberForm.getGrade());
            member.setStatus(memberForm.getStatus());
            member.setAddress(memberForm.getAddress());
            member.setReceiveEmail(memberForm.getReceiveEmail());
            member.setOpenProfile(memberForm.getOpenProfile());
            member.setReceiveNotify(memberForm.getReceiveNotify());
            member.setLoginType(memberForm.getLoginType());
            // 필요에 따라 추가적인 필드 업데이트
            memberRepository.save(member);
            return true;
        }).orElse(false);
    }

    /**
     * 회원의 상태를 업데이트하는 메서드
     *
     * @param id        회원 ID
     * @param newStatus 새로운 상태
     * @return 업데이트 성공 여부
     */
    public boolean updateMemberStatus(Long id, String newStatus) {
        if (!"활성".equals(newStatus) && !"정지".equals(newStatus) && !"탈퇴".equals(newStatus)) {
            return false; // 유효하지 않은 상태
        }

        return memberRepository.findById(id).map(member -> {
            member.setStatus(newStatus);
            memberRepository.save(member);
            return true;
        }).orElse(false);
    }

    // 추가적인 회원 관리 메서드들...
    @Transactional
    public Page<LoginHistoryDTO> searchMemberLogs(String searchQuery, String hisDeviceOs, String hisBrowser, Pageable pageable) {
        Page<LoginHistoryEntity> loginHistoryEntities = loginHistoryRepository.searchMemberLogs(searchQuery, hisDeviceOs, hisBrowser, pageable);
        return loginHistoryEntities.map(LoginHistoryDTO::toDTO);
    }

    @Transactional
    public Page<LoginHistoryDTO> getAllMemberLogs(Pageable pageable) {
        Page<LoginHistoryEntity> loginHistoryEntities = loginHistoryRepository.findAll(pageable);
        return loginHistoryEntities.map(LoginHistoryDTO::toDTO);
    }
}
