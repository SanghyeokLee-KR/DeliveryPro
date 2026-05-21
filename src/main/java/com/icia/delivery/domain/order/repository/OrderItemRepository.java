package com.icia.delivery.domain.order.repository;

import com.icia.delivery.domain.order.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long>{


    @Query("SELECT oi FROM OrderItemEntity oi WHERE oi.orderId = :orderId")
    List<OrderItemEntity> findOrderItemsByOrderId(@Param("orderId") Long orderId);

}
