package com.icia.delivery.service.member;

import com.icia.delivery.dao.member.LoginHistoryRepository;
import com.icia.delivery.dao.member.MemberRepository;
import com.icia.delivery.dto.member.LoginHistoryDTO;
import com.icia.delivery.dto.member.LoginHistoryEntity;
import com.icia.delivery.dto.member.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoginHistoryService {

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @Autowired
    private MemberRepository memberRepository; // 회원 정보 조회를 위한 MemberRepository

    /**
     * 모든 로그인 내역 조회
     *
     * @return List<LoginHistoryDTO>
     */
    public List<LoginHistoryDTO> getAllLoginHistories() {
        List<LoginHistoryEntity> entities = loginHistoryRepository.findAll();
        return entities.stream()
                .map(LoginHistoryDTO::toDTO) // Entity → DTO 변환
                .collect(Collectors.toList());
    }


    /**
     * 새로운 로그인 내역 저장
     *
     * @param dto 로그인 내역 DTO
     * @return LoginHistoryDTO
     */
    public LoginHistoryDTO saveLoginHistory(LoginHistoryDTO dto) {
        // DTO에 포함된 회원 ID로 회원 엔티티 조회
        MemberEntity member = memberRepository.findById(dto.getHisMid())
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));

        // DTO → Entity 변환 (MemberEntity 포함)
        LoginHistoryEntity entity = LoginHistoryEntity.toEntity(dto, member);
        entity.setHisLoginDate(LocalDateTime.now()); // 현재 시간으로 설정

        // 저장 후 저장된 엔티티를 DTO로 변환하여 반환
        LoginHistoryEntity savedEntity = loginHistoryRepository.save(entity);
        return LoginHistoryDTO.toDTO(savedEntity);
    }

    @Transactional(readOnly = true)
    public List<LoginHistoryDTO> getAllLoginHistories(Long mId) {
        return loginHistoryRepository.findLoginHistoriesByMemberId(mId);
    }
}
