package com.icia.delivery.dao.rider;

import com.icia.delivery.dto.rider.RiderAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiderAccounRepository extends JpaRepository<RiderAccountEntity, Long> {
    List<RiderAccountEntity> findByRiderNo(Long riderNo);
}
