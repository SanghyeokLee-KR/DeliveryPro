package com.icia.delivery.dao.admin;

import com.icia.delivery.dto.admin.AdvertisementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertisementRepository extends JpaRepository<AdvertisementEntity, Long> {
    // 광고 5개 관리
}
