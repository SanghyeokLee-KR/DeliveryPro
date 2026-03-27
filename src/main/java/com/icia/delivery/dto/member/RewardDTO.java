package com.icia.delivery.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardDTO {

    private Long rewardId;
    private Long memId;
    private Long rewardAmount;

    public static RewardDTO toDTO(RewardEntity entity) {
        RewardDTO dto = new RewardDTO();

        dto.setRewardId(entity.getRewardId());
        dto.setMemId(entity.getMemId());
        dto.setRewardAmount(entity.getRewardAmount());

        return dto;
    }

}
