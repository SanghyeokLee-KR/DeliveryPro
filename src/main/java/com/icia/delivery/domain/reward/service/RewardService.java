package com.icia.delivery.domain.reward.service;

import com.icia.delivery.domain.member.repository.MemberRepository;
import com.icia.delivery.domain.reward.repository.RewardRepository;
import com.icia.delivery.domain.member.entity.MemberEntity;
import com.icia.delivery.domain.reward.dto.RewardDTO;
import com.icia.delivery.domain.reward.entity.RewardEntity;
import com.icia.delivery.global.exception.BusinessException;
import com.icia.delivery.global.exception.ErrorCode;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final MemberRepository memberRepository;

    private final HttpSession session;

    @Transactional(readOnly = true)
    public RewardDTO memberReward() {

        Long memId = getSessionMemberId();

        Optional<RewardEntity> rewardEntity = rewardRepository.findBymemId(memId);

        RewardDTO dto = new RewardDTO();

        if(rewardEntity.isPresent()) {
            RewardEntity reward = rewardEntity.get();
            dto.setRewardId(reward.getRewardId());
            dto.setRewardAmount(reward.getRewardAmount());
            dto.setMemId(reward.getMemId());
        }

        return dto;
    }

    @Transactional
    public String updateMemberReward() {
        // 세션에서 memId를 가져옵니다.
        Long memId = getSessionMemberId();

        // memId에 해당하는 RewardEntity를 조회합니다.
        Optional<RewardEntity> rewardEntityOpt = rewardRepository.findBymemId(memId);

        if (rewardEntityOpt.isPresent()) {
            // RewardEntity를 가져옵니다.
            RewardEntity rewardEntity = rewardEntityOpt.get();

            // RewardEntity에서 rewardAmount를 가져옵니다.
            Long rewardAmount = (long) rewardEntity.getRewardAmount();

            // 등급을 결정합니다.
            String newGrade = determineGrade(rewardAmount);

            // MemberEntity를 가져옵니다. (여기서 MemberEntity는 이미 memId와 연결되어 있다고 가정)
            Optional<MemberEntity> memberEntityOpt = memberRepository.findById(memId);

            if (memberEntityOpt.isPresent()) {
                MemberEntity memberEntity = memberEntityOpt.get();

                // 새로운 등급을 업데이트합니다.
                memberEntity.setGrade(newGrade);

                // MemberEntity를 업데이트합니다.
                memberRepository.save(memberEntity);

                return "등급이 성공적으로 업데이트되었습니다!";
            } else {
                return "해당 회원을 찾을 수 없습니다.";
            }
        } else {
            return "리워드 정보를 찾을 수 없습니다.";
        }
    }

    // rewardAmount에 따라 등급을 결정하는 메서드
    private String determineGrade(Long rewardAmount) {
        if (rewardAmount < 50000) {
            return "welcome";
        } else if (rewardAmount >= 50000 && rewardAmount < 100000) {
            return "family";
        } else if (rewardAmount >= 100000 && rewardAmount < 200000) {
            return "vip";
        } else {
            return "vvip";
        }
    }

    private Long getSessionMemberId() {
        Long memId = (Long) session.getAttribute("mem_id");
        if (memId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Login is required.");
        }
        return memId;
    }

}
