package com.icia.delivery.domain.reward.repository;

import com.icia.delivery.domain.reward.entity.RewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RewardRepository extends JpaRepository<RewardEntity, Long> {


    Optional<RewardEntity> findBymemId(Long memId);
}
