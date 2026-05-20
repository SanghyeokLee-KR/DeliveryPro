package com.icia.delivery.controller.rider;

import com.icia.delivery.dto.member.OrderEntity;
import com.icia.delivery.dto.rider.DeliveryGroupDTO;
import com.icia.delivery.service.member.OrderService;
import com.icia.delivery.service.rider.DeliveryGroupService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryGroupApiController {

    @Autowired
    private DeliveryGroupService deliveryGroupService;

    @Autowired
    private OrderService orderService;  // 주문 상태 업데이트를 위한 서비스

    @GetMapping("/{preId}")
    public List<DeliveryGroupDTO> getDeliveryData(@PathVariable("preId") Long preId) {
        // 필요하다면 preId(가게 ID)를 기반으로 필터링할 수 있습니다.
        List<DeliveryGroupDTO> groupOrders = deliveryGroupService.getProcessedGroupOrders();
        List<DeliveryGroupDTO> singleOrders = deliveryGroupService.getProcessedSingleOrders();
        List<DeliveryGroupDTO> combined = new ArrayList<>();
        combined.addAll(groupOrders);
        combined.addAll(singleOrders);
        return combined;
    }

    @PostMapping("/accept/{deliveryId}")
    public ResponseEntity<Map<String, Object>> acceptDelivery(
            @PathVariable("deliveryId") Long deliveryId, HttpSession session) {

        Long riderNo = (Long) session.getAttribute("rider_no");
        if (riderNo == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "라이더 정보가 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        deliveryGroupService.acceptDeliveryGroup(deliveryId, riderNo);

        Map<String, Object> result = new HashMap<>();
        result.put("riderNo", riderNo);
        return ResponseEntity.ok(result);
    }

    // --- 그룹(여러 배달ID) 수락 처리 엔드포인트 ---
    @PostMapping("/accept")
    public ResponseEntity<Map<String, Object>> acceptDeliveryGroup(
            @RequestBody Map<String, List<Long>> payload, HttpSession session) {

        Long riderNo = (Long) session.getAttribute("rider_no");
        if (riderNo == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "라이더 정보가 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        List<Long> deliveryIds = payload.get("deliveryIds");
        if (deliveryIds == null || deliveryIds.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "전달된 배달 ID가 없습니다.");
            return ResponseEntity.badRequest().body(error);
        }

        // 전달받은 각 배달 ID에 대해 수락 처리
        for (Long id : deliveryIds) {
            deliveryGroupService.acceptDeliveryGroup(id, riderNo);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("riderNo", riderNo);
        result.put("acceptedDeliveryIds", deliveryIds);
        return ResponseEntity.ok(result);
    }

    /**
     * 개별 주문(order)의 배달 상태를 "배달완료"로 업데이트합니다.
     * 프론트엔드에서 각 구간(각 고객 집 도착 시)에 대해 orderId를 전달하여 호출합니다.
     */
    @PostMapping("/complete/order/{orderId}")
    public ResponseEntity<Map<String, Object>> completeOrder(
            @PathVariable("orderId") Long orderId, HttpSession session) {

        Long riderNo = (Long) session.getAttribute("rider_no");
        if (riderNo == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "라이더 정보가 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        try {
            orderService.completeOrder(orderId, riderNo);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("deliveryStatus", "배달완료");
        return ResponseEntity.ok(result);
    }

    /**
     * 그룹(묶음 배달)의 배달 상태를 "배달완료"로 업데이트합니다.
     * 최종 도착 시 호출됩니다.
     */
    @PostMapping("/complete/group/{deliveryId}")
    public ResponseEntity<Map<String, Object>> completeDeliveryGroup(
            @PathVariable("deliveryId") Long deliveryId, HttpSession session) {

        Long riderNo = (Long) session.getAttribute("rider_no");
        if (riderNo == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "라이더 정보가 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        try {
            deliveryGroupService.completeDeliveryGroup(deliveryId, riderNo);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("deliveryId", deliveryId);
        result.put("deliveryStatus", "배달완료");
        return ResponseEntity.ok(result);
    }
}
