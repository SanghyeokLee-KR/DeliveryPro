package com.icia.delivery.dao.member;

import com.icia.delivery.dto.member.OrderEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {


    @Query("SELECT o.orderId, o.orderCreatedAt, s.preStoName, m.menuName, m.menuPictureUrl, m.preStoId " +
            "FROM OrderEntity o, PreStoreMenuEntity m, PreStoreEntity s " +
            "WHERE o.memId = :memId AND m.menuId = o.menuId AND s.preStoId = m.preStoId")
    List<Object[]> findOrderSummariesByMemId(Long memId);

    @Query("SELECT o FROM OrderEntity o WHERE o.preStoId = :preStoId ORDER BY o.orderId ASC")
    List<OrderEntity> findByPreStoId(@Param("preStoId") Long prestoId);

    Optional<OrderEntity> findByOrderId(Long orderId);


    @Query(value = "SELECT o.order_id, o.delivery_type, o.delivery_status, m.mem_address, COUNT(oi.menu_id), " +
            "o.delivery_message, s.pre_sto_name, s.pre_sto_address " +
            "FROM orders o " +
            "JOIN orderitem oi ON o.order_id = oi.order_id " +
            "JOIN member m ON o.mem_id = m.mem_id " +
            "JOIN pre_store s ON o.pre_sto_id = s.pre_sto_id " + // 가게 정보 추가
            "WHERE o.order_id = :orderId " +
            "GROUP BY o.order_id, o.delivery_type, o.delivery_status, m.mem_address, o.delivery_message, s.pre_sto_name, s.pre_sto_address",
            nativeQuery = true)
    List<Object[]> findOrderWithMenuCount(@Param("orderId") Long orderId);



    @Query(value = "SELECT o.order_id, s.pre_sto_address, o.delivery_fee, o.delivery_message, " +
            "o.payment_method, oi.total_price, m.mem_address " +
            "FROM orders o " +
            "JOIN pre_store s ON o.pre_sto_id = s.pre_sto_id " +
            "JOIN orderitem oi ON o.order_id = oi.order_id " +
            "JOIN member m ON o.mem_id = m.mem_id " +
            "WHERE o.order_id = :orderId", nativeQuery = true)
    List<Object[]> findOrderWithStore(@Param("orderId") Long orderId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE orders SET delivery_status = '배달중' WHERE order_id = :orderId", nativeQuery = true)
    void updateDeliveryStatusToInProgress(@Param("orderId") Long orderId);

    List<OrderEntity> findByPreStoIdAndOrderCreatedAtBetween(Long preStoId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query("SELECT o.memId FROM OrderEntity o WHERE o.orderId = :orderId")
    Optional<Long> findMemIdByOrderId(Long orderId);

    @Query("SELECT o FROM OrderEntity o WHERE o.deliveryStatus = :status")
    List<OrderEntity> findByDeliveryStatusTrimmed(@Param("status") String status);

}