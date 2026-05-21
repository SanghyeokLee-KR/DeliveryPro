package com.icia.delivery.domain.admin.repository;

import com.icia.delivery.domain.admin.entity.AdvertisementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertisementRepository extends JpaRepository<AdvertisementEntity, Long> {
    // 광고 5개 관리
}
