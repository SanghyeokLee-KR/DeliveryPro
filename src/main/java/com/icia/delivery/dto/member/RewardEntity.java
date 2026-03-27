package com.icia.delivery.dto.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reward")
public class RewardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reward_id")
    private Long rewardId;

    @Column(name = "mem_id")
    private Long memId;

    @Column(name = "reward_amount")
    private Long rewardAmount;

    public static RewardEntity toEntity(RewardDTO dto) {
        RewardEntity entity = new RewardEntity();

        entity.setRewardId(dto.getRewardId());
        entity.setMemId(dto.getMemId());
        entity.setRewardAmount(dto.getRewardAmount());

        return entity;
    }

}
