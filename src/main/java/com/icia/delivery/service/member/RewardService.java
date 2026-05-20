package com.icia.delivery.service.member;

import com.icia.delivery.dao.member.MemberRepository;
import com.icia.delivery.dao.member.RewardRepository;
import com.icia.delivery.dto.member.MemberEntity;
import com.icia.delivery.dto.member.RewardDTO;
import com.icia.delivery.dto.member.RewardEntity;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final MemberRepository memberRepository;

    private final HttpSession session;

    public RewardDTO memberReward() {

        Long memId = (Long) session.getAttribute("mem_id");

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
        Long memId = (Long) session.getAttribute("mem_id");

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


}
