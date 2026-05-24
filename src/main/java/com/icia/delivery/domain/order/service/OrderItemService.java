package com.icia.delivery.domain.order.service;


import com.icia.delivery.domain.order.dto.OrderItemDTO;
import com.icia.delivery.domain.order.entity.OrderItemEntity;
import com.icia.delivery.domain.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {

     private final OrderItemRepository oirepo;

    @Transactional(readOnly = true)
    public List<OrderItemDTO> findOrderById(Long orderId) {

        List<OrderItemEntity> orderEntityList = oirepo.findOrderItemsByOrderId(orderId);
        return orderEntityList.stream()
                .map(OrderItemDTO::toDTO) // Entity → DTO 변환
                .collect(Collectors.toList());
    }

    }
