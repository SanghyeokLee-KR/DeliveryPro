package com.icia.delivery.domain.deliverygroup.controller;

import com.icia.delivery.domain.deliverygroup.dto.DeliveryGroupDTO;
import com.icia.delivery.domain.deliverygroup.service.DeliveryGroupService;
import com.icia.delivery.domain.order.service.OrderService;
import com.icia.delivery.global.exception.BusinessException;
import com.icia.delivery.global.exception.ErrorCode;
import com.icia.delivery.global.response.ApiResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryGroupApiController {

    @Autowired
    private DeliveryGroupService deliveryGroupService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/{preId}")
    public ResponseEntity<ApiResponse<List<DeliveryGroupDTO>>> getDeliveryData(@PathVariable("preId") Long preId) {
        List<DeliveryGroupDTO> groupOrders = deliveryGroupService.getProcessedGroupOrders();
        List<DeliveryGroupDTO> singleOrders = deliveryGroupService.getProcessedSingleOrders();
        List<DeliveryGroupDTO> combined = new ArrayList<>();
        combined.addAll(groupOrders);
        combined.addAll(singleOrders);
        return ResponseEntity.ok(ApiResponse.success(combined));
    }

    @PostMapping("/accept/{deliveryId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> acceptDelivery(
            @PathVariable("deliveryId") Long deliveryId, HttpSession session) {

        Long riderNo = getRiderNo(session);
        acceptDeliveryGroupOrThrow(deliveryId, riderNo);

        Map<String, Object> result = new HashMap<>();
        result.put("riderNo", riderNo);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<Map<String, Object>>> acceptDeliveryGroup(
            @RequestBody Map<String, List<Long>> payload, HttpSession session) {

        Long riderNo = getRiderNo(session);
        List<Long> deliveryIds = payload != null ? payload.get("deliveryIds") : null;
        if (deliveryIds == null || deliveryIds.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Delivery ids are required.");
        }

        for (Long id : deliveryIds) {
            acceptDeliveryGroupOrThrow(id, riderNo);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("riderNo", riderNo);
        result.put("acceptedDeliveryIds", deliveryIds);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/complete/order/{orderId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> completeOrder(
            @PathVariable("orderId") Long orderId, HttpSession session) {

        Long riderNo = getRiderNo(session);
        try {
            orderService.completeOrder(orderId, riderNo);
        } catch (RuntimeException e) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, e.getMessage(), e);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("deliveryStatus", "배달완료");
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/complete/group/{deliveryId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> completeDeliveryGroup(
            @PathVariable("deliveryId") Long deliveryId, HttpSession session) {

        Long riderNo = getRiderNo(session);
        try {
            deliveryGroupService.completeDeliveryGroup(deliveryId, riderNo);
        } catch (RuntimeException e) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, e.getMessage(), e);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("deliveryId", deliveryId);
        result.put("deliveryStatus", "배달완료");
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    private Long getRiderNo(HttpSession session) {
        Long riderNo = (Long) session.getAttribute("rider_no");
        if (riderNo == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Rider session is required.");
        }
        return riderNo;
    }

    private void acceptDeliveryGroupOrThrow(Long deliveryId, Long riderNo) {
        try {
            deliveryGroupService.acceptDeliveryGroup(deliveryId, riderNo);
        } catch (RuntimeException e) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
