package com.icia.delivery.domain.reward.controller;

import com.icia.delivery.domain.reward.dto.RewardDTO;
import com.icia.delivery.domain.reward.service.RewardService;
import com.icia.delivery.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    // memReward
    @PostMapping("/memReward")
    public ResponseEntity<ApiResponse<RewardDTO>> memReward(){
        return ResponseEntity.ok(ApiResponse.success(rewardService.memberReward()));
    }

    // updateReward
    @PostMapping("/updateReward")
    public ResponseEntity<ApiResponse<String>> updateReward(){
        return ResponseEntity.ok(ApiResponse.success(rewardService.updateMemberReward()));
    }

}
