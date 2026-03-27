package com.icia.delivery.dao.rider;

import com.icia.delivery.dto.rider.DeliveryGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryGroupRepository extends JpaRepository<DeliveryGroupEntity, Long> {

    Optional<DeliveryGroupEntity> findByStoreIdAndDeliveryTypeAndDeliveryStatus(
            Long storeId,
            String deliveryType,
            String deliveryStatus
    );

    // 추가: delivery_status가 지정된 여러 상태("배달중", "배달완료")인 경우 조회
    List<DeliveryGroupEntity> findByDeliveryStatusIn(List<String> statuses);

    // 추가: delivery_type과 함께 특정 여러 delivery_status("배달중", "배달완료")인 경우 조회
    List<DeliveryGroupEntity> findByDeliveryTypeAndDeliveryStatusIn(String deliveryType, List<String> statuses);

    Optional<DeliveryGroupEntity> findById(Long deliveryGroupId);
}
