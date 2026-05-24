package com.icia.delivery.domain.order.controller;

import com.icia.delivery.domain.deliverygroup.dto.GroupRiderCallRequest;
import com.icia.delivery.domain.order.dto.OrderDTO;
import com.icia.delivery.domain.order.service.OrderService;
import com.icia.delivery.global.exception.BusinessException;
import com.icia.delivery.global.exception.ErrorCode;
import com.icia.delivery.global.response.ApiResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final HttpSession session;

    @PostMapping
    public ModelAndView createOrder(@ModelAttribute OrderDTO orderDTO) {
        return orderService.createOrder(orderDTO);
    }

    @PostMapping("detail/{orderId}")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> detailOrder(@PathVariable("orderId") Long orderId) {
        List<OrderDTO> dtoList = orderService.findOrderById(orderId);
        session.setAttribute("orderId", orderId);
        return ResponseEntity.ok(ApiResponse.success(dtoList));
    }

    @PostMapping("orderList/{preStoId}")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> orderList(@PathVariable("preStoId") Long preStoId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.orderList(preStoId)));
    }

    @PostMapping("/{memId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getOrderSummaries(@PathVariable Long memId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderSummariesByMemberId(memId)));
    }

    @PostMapping("/{orderId}/{action}")
    public ResponseEntity<ApiResponse<Map<String, String>>> acceptOrder(
            @PathVariable Long orderId,
            @PathVariable String action
    ) {
        runOrderAction(() -> orderService.acceptOrder(orderId, action));

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "주문 처리 성공!");
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{orderId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectOrder(@PathVariable Long orderId) {
        runOrderAction(() -> orderService.rejectOrder(orderId));
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PostMapping("/delivery/{orderId}")
    public ResponseEntity<ApiResponse<Object>> getOrders(@PathVariable Long orderId) {
        List<?> orders = orderService.findOrders(orderId);
        Object response = orders.isEmpty() ? Collections.emptyList() : Map.of("data", orders);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/accept/{orderId}")
    public ResponseEntity<ApiResponse<Object>> accept(@PathVariable Long orderId) {
        List<?> orders = orderService.acceptOrders(orderId);
        Object response = orders.isEmpty() ? Collections.emptyList() : Map.of("data", orders);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/storeOrderList")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> storeOrderList() {
        return ResponseEntity.ok(ApiResponse.success(orderService.storeOrderList()));
    }

    @PostMapping("/riderCall")
    public ResponseEntity<ApiResponse<String>> riderCall(@RequestParam("orderId") Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.riderCall(orderId)));
    }

    @PostMapping("/riderOrderList")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> riderOrderList() {
        return ResponseEntity.ok(ApiResponse.success(orderService.riderOrderList()));
    }

    @PostMapping("/groupRiderCall")
    public ResponseEntity<ApiResponse<String>> groupRiderCall(@RequestBody GroupRiderCallRequest request) {
        List<Long> orderIds = request != null ? request.getOrderIds() : null;
        String callTime = request != null ? request.getCallTime() : null;
        if (orderIds == null || orderIds.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "No orders provided for group rider call.");
        }

        runOrderAction(() -> orderService.groupRiderCall(orderIds, callTime));
        return ResponseEntity.ok(ApiResponse.success(
                "Group rider call initiated for orders: " + orderIds + " at " + callTime
        ));
    }

    @PostMapping("/batchAccept")
    public ResponseEntity<ApiResponse<String>> acceptBatchOrders(@RequestBody Map<String, Object> requestBody) {
        Object storeIdObj = requestBody != null ? requestBody.get("preStoId") : null;
        if (storeIdObj == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "No preStoId provided.");
        }
        Long preStoId = Long.valueOf(storeIdObj.toString());

        Object orderIdsObj = requestBody.get("orderIds");
        if (!(orderIdsObj instanceof List)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid orderIds format.");
        }

        @SuppressWarnings("unchecked")
        List<Long> orderIds = (List<Long>) orderIdsObj;
        if (orderIds.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "No orderIds to acceptBatch.");
        }

        runOrderAction(() -> orderService.acceptBatchOrders(preStoId, orderIds));
        return ResponseEntity.ok(ApiResponse.success(
                "Batch accept complete for storeId=" + preStoId + ", orders=" + orderIds
        ));
    }

    private void runOrderAction(Runnable action) {
        try {
            action.run();
        } catch (BusinessException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
