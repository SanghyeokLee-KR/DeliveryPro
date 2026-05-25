// src/main/java/com/icia/delivery/service/admin/OperatorService.java
package com.icia.delivery.domain.admin.service;

import com.icia.delivery.domain.member.repository.MemberRepository;
import com.icia.delivery.domain.member.repository.MemberUserIdProjection;
import com.icia.delivery.domain.loginhistory.dto.LoginHistoryDTO;
import com.icia.delivery.domain.loginhistory.entity.LoginHistoryEntity;
import com.icia.delivery.domain.loginhistory.repository.LoginHistoryRepository;
import com.icia.delivery.domain.member.dto.MemberDTO;
import com.icia.delivery.domain.member.entity.MemberEntity;
import com.icia.delivery.global.exception.BusinessException;
import com.icia.delivery.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OperatorService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    // 로그인 기록 순서에 맞춰 회원 userId 목록을 조회
    @Transactional(readOnly = true)
    public List<String> getMemberUserIdsByLoginHistories(List<LoginHistoryDTO> loginHistories) {
        if (loginHistories == null || loginHistories.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> memberIds = loginHistories.stream()
                .map(LoginHistoryDTO::getHisMid)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (memberIds.isEmpty()) {
            return loginHistories.stream()
                    .map(log -> "")
                    .collect(Collectors.toList());
        }

        Map<Long, String> userIdByMemberId = new HashMap<>();
        for (MemberUserIdProjection projection : memberRepository.findMemberUserIdsByIds(memberIds)) {
            userIdByMemberId.put(projection.getMId(), projection.getUserId());
        }

        return loginHistories.stream()
                .map(log -> {
                    String userId = userIdByMemberId.get(log.getHisMid());
                    return userId != null ? userId : "";
                })
                .collect(Collectors.toList());
    }


    /**
     * 모든 회원을 페이징하여 조회하는 메서드
     *
     * @param pageable 페이징 정보
     * @return 페이징된 회원 리스트 DTO
     */
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public MemberDTO getMemberById(Long id) {
        return memberRepository.findById(id)
                .map(MemberDTO::toDTO)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "회원 정보를 찾을 수 없습니다. memberId=" + id));
    }

    /**
     * 회원의 정보를 업데이트하는 메서드
     *
     * @param id         회원 ID
     * @param memberForm 수정된 회원 정보가 담긴 MemberDTO
     * @return 업데이트 성공 여부
     */
    @Transactional
    public boolean updateMemberInfo(Long id, MemberDTO memberForm) {
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "회원 정보를 찾을 수 없습니다. memberId=" + id));

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
    }

    /**
     * 회원의 상태를 업데이트하는 메서드
     *
     * @param id        회원 ID
     * @param newStatus 새로운 상태
     * @return 업데이트 성공 여부
     */
    @Transactional
    public boolean updateMemberStatus(Long id, String newStatus) {
        if (!"활성".equals(newStatus) && !"정지".equals(newStatus) && !"탈퇴".equals(newStatus)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "유효하지 않은 회원 상태입니다.");
        }

        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "회원 정보를 찾을 수 없습니다. memberId=" + id));
        member.setStatus(newStatus);
        memberRepository.save(member);
        return true;
    }

    // 추가적인 회원 관리 메서드들...
    @Transactional(readOnly = true)
    public Page<LoginHistoryDTO> searchMemberLogs(String searchQuery, String hisDeviceOs, String hisBrowser, Pageable pageable) {
        Page<LoginHistoryEntity> loginHistoryEntities = loginHistoryRepository.searchMemberLogs(searchQuery, hisDeviceOs, hisBrowser, pageable);
        return toLoginHistoryDTOPage(loginHistoryEntities);
    }

    @Transactional(readOnly = true)
    public Page<LoginHistoryDTO> getAllMemberLogs(Pageable pageable) {
        Page<LoginHistoryEntity> loginHistoryEntities = loginHistoryRepository.findAll(pageable);
        return toLoginHistoryDTOPage(loginHistoryEntities);
    }

    private Page<LoginHistoryDTO> toLoginHistoryDTOPage(Page<LoginHistoryEntity> loginHistoryEntities) {
        return loginHistoryEntities.map(LoginHistoryDTO::toDTO);
    }
}
