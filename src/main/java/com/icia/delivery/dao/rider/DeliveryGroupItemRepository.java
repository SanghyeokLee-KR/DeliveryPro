package com.icia.delivery.dao.rider;

import com.icia.delivery.dto.rider.DeliveryGroupItemEntity;
import com.icia.delivery.dto.rider.DeliveryGroupItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryGroupItemRepository extends JpaRepository<DeliveryGroupItemEntity, DeliveryGroupItemId> {

    // 단일 orderId에 해당하는 DeliveryGroupItemEntity 목록 조회
    @Query("SELECT dgi FROM DeliveryGroupItemEntity dgi WHERE dgi.orderId = :orderId")
    List<DeliveryGroupItemEntity> findAllByOrderId(@Param("orderId") Long orderId);

    // 여러 orderId에 해당하는 DeliveryGroupItemEntity 목록 조회
    @Query("SELECT dgi FROM DeliveryGroupItemEntity dgi WHERE dgi.orderId IN :orderIds")
    List<DeliveryGroupItemEntity> findAllByOrderIdIn(@Param("orderIds") List<Long> orderIds);

    // 단일 deliveryId에 해당하는 DeliveryGroupItemEntity 목록 조회
    @Query("SELECT dgi FROM DeliveryGroupItemEntity dgi WHERE dgi.deliveryId = :deliveryId")
    List<DeliveryGroupItemEntity> findAllByDeliveryId(@Param("deliveryId") Long deliveryId);
}
