package com.icia.delivery.domain.reward.controller;

import com.icia.delivery.domain.reward.dto.RewardDTO;
import com.icia.delivery.domain.reward.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    // memReward
    @PostMapping("/memReward")
    public RewardDTO memReward(){
        return rewardService.memberReward();
    }

    // updateReward
    @PostMapping("/updateReward")
    public String updateReward(){
        return rewardService.updateMemberReward();
    }

}
