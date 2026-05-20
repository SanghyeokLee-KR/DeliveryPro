package com.icia.delivery.dao.member;

import com.icia.delivery.dto.member.RewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RewardRepository extends JpaRepository<RewardEntity, Long> {


    Optional<RewardEntity> findBymemId(Long memId);
}
