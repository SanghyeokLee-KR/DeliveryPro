package com.icia.delivery.controller.member;

import com.icia.delivery.dto.member.OrderDTO;
import com.icia.delivery.dto.rider.GroupRiderCallRequest;
import com.icia.delivery.service.member.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OrderController
 *  - "/orders" 관련 요청을 처리하는 컨트롤러
 *  - 주문 생성, 상세, 단건 수락/거절, 묶음배달 배치 수락 등
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;  // OrderService 주입
    private final HttpSession session;        // 세션 사용

    /**
     * 1) 주문 생성 (POST /orders)
     */
    @PostMapping
    public ModelAndView createOrder(@ModelAttribute OrderDTO orderDTO) {
        return orderService.createOrder(orderDTO);
    }

    /**
     * 2) 주문 상세 조회 (POST /orders/detail/{orderId})
     */
    @PostMapping("detail/{orderId}")
    public ResponseEntity<List<OrderDTO>> detailOrder(@PathVariable("orderId") Long orderId) {
        List<OrderDTO> dtoList = orderService.findOrderById(orderId);
        session.setAttribute("orderId", orderId);
        return ResponseEntity.ok(dtoList);
    }

    /**
     * 3) 특정 가게(preStoId)의 주문 목록 (POST /orders/orderList/{preStoId})
     */
    @PostMapping("orderList/{preStoId}")
    public ResponseEntity<List<OrderDTO>> orderList(@PathVariable("preStoId") Long preStoId) {
        List<OrderDTO> orderList = orderService.orderList(preStoId);
        return ResponseEntity.ok(orderList);
    }

    /**
     * 4) 회원 마이페이지: 주문 요약 (POST /orders/{memId})
     */
    @PostMapping("/{memId}")
    public List<Map<String, Object>> getOrderSummaries(@PathVariable Long memId) {
        return orderService.getOrderSummariesByMemberId(memId);
    }

    /**
     * 5) 단건 주문 수락/거절 (POST /orders/{orderId}/{action})
     */
    @PostMapping("/{orderId}/{action}")
    public ResponseEntity<Map<String, String>> acceptOrder(
            @PathVariable Long orderId,
            @PathVariable String action
    ) {
        orderService.acceptOrder(orderId, action);
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "주문 처리 성공!");
        return ResponseEntity.ok(response);
    }

    /**
     * 6) 단건 주문 거절 (POST /orders/{orderId}/reject)
     */
    @PostMapping("/{orderId}/reject")
    public ResponseEntity<Map<String, String>> rejectOrder(@PathVariable Long orderId) {
        orderService.rejectOrder(orderId);
        return ResponseEntity.ok().build();
    }

    /**
     * 7) 배달 정보 조회 (POST /orders/delivery/{orderId})
     */
    @PostMapping("/delivery/{orderId}")
    public ResponseEntity<?> getOrders(@PathVariable Long orderId) {
        List<?> orders = orderService.findOrders(orderId);
        if (orders.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(Map.of("data", orders));
    }

    /**
     * 8) 배달 수락 후 처리 (POST /orders/accept/{orderId})
     */
    @PostMapping("/accept/{orderId}")
    public ResponseEntity<?> accept(@PathVariable Long orderId) {
        List<?> orders = orderService.acceptOrders(orderId);
        if (orders.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(Map.of("data", orders));
    }

    /**
     * 9) 가게 측 주문 목록 조회 (POST /orders/storeOrderList)
     */
    @PostMapping("/storeOrderList")
    public List<OrderDTO> storeOrderList(){
        return orderService.storeOrderList();
    }

    /**
     * 10) 단건 주문 라이더 호출 (POST /orders/riderCall)
     */
    @PostMapping("/riderCall")
    public String riderCall(@RequestParam("orderId") Long orderId){
        return orderService.riderCall(orderId);
    }

    /**
     * 11) 라이더가 볼 주문 목록 (POST /orders/riderOrderList)
     */
    @PostMapping("/riderOrderList")
    public List<OrderDTO> riderOrderList(){
        return orderService.riderOrderList();
    }

    /**
     * 12) "그룹(묶음) 라이더 호출" (POST /orders/groupRiderCall)
     */
    @PostMapping("/groupRiderCall")
    public ResponseEntity<String> groupRiderCall(@RequestBody GroupRiderCallRequest request) {
        List<Long> orderIds = request.getOrderIds();
        String callTime = request.getCallTime();  // 예: "2025-02-02 03:57:12"
        if (orderIds == null || orderIds.isEmpty()) {
            return ResponseEntity.badRequest().body("No orders provided for group rider call.");
        }
        orderService.groupRiderCall(orderIds, callTime);
        return ResponseEntity.ok("Group rider call initiated for orders: " + orderIds + " at " + callTime);
    }

    /**
     * 13) "묶음배달" 한 번에 여러 주문을 하나의 delivery_group에 묶기 (POST /orders/batchAccept)
     *     Request JSON 예: { "preStoId": 2, "orderIds": [10,11,12] }
     */
    @PostMapping("/batchAccept")
    public ResponseEntity<String> acceptBatchOrders(@RequestBody Map<String, Object> requestBody) {
        Object storeIdObj = requestBody.get("preStoId");
        if (storeIdObj == null) {
            return ResponseEntity.badRequest().body("No preStoId provided.");
        }
        Long preStoId = Long.valueOf(storeIdObj.toString());

        Object orderIdsObj = requestBody.get("orderIds");
        if (!(orderIdsObj instanceof List)) {
            return ResponseEntity.badRequest().body("Invalid orderIds format.");
        }
        @SuppressWarnings("unchecked")
        List<Long> orderIds = (List<Long>) orderIdsObj;
        if (orderIds.isEmpty()) {
            return ResponseEntity.badRequest().body("No orderIds to acceptBatch.");
        }

        orderService.acceptBatchOrders(preStoId, orderIds);
        return ResponseEntity.ok("Batch accept complete for storeId=" + preStoId + ", orders=" + orderIds);
    }

}
