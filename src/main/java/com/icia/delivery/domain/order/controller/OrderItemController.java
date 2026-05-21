package com.icia.delivery.domain.order.controller;


import com.icia.delivery.domain.order.dto.OrderItemDTO;
import com.icia.delivery.domain.order.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orderItem")
@RequiredArgsConstructor
public class OrderItemController {


    private final OrderItemService oisv;


    @PostMapping("{orderId}")
    public ResponseEntity<List<OrderItemDTO>> detailOrder(@PathVariable("orderId") Long orderId) {
        List<OrderItemDTO> dtoList = oisv.findOrderById(orderId);
        return ResponseEntity.ok(dtoList);
    }
}
