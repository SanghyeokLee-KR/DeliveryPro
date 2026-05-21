package com.icia.delivery.domain.rider.repository;

import com.icia.delivery.domain.rider.entity.RiderAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiderAccounRepository extends JpaRepository<RiderAccountEntity, Long> {
    List<RiderAccountEntity> findByRiderNo(Long riderNo);
}
