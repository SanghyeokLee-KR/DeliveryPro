package com.icia.delivery.controller.member;

import com.icia.delivery.dto.member.RewardDTO;
import com.icia.delivery.service.member.RewardService;
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
