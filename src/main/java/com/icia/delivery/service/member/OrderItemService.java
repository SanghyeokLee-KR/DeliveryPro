package com.icia.delivery.service.member;


import com.icia.delivery.dao.member.OrderItemRepository;
import com.icia.delivery.dto.member.OrderItemDTO;
import com.icia.delivery.dto.member.OrderItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {

     private final OrderItemRepository oirepo;

    public List<OrderItemDTO> findOrderById(Long orderId) {

        List<OrderItemEntity> orderEntityList = oirepo.findOrderItemsByOrderId(orderId);
        return orderEntityList.stream()
                .map(OrderItemDTO::toDTO) // Entity → DTO 변환
                .collect(Collectors.toList());
    }

    }

