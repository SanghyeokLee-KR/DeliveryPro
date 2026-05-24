package com.icia.delivery.domain.store.controller;

import com.icia.delivery.domain.store.dto.PreStoreDTO;
import com.icia.delivery.domain.review.service.ReviewService;
import com.icia.delivery.domain.store.service.StoreService;
import com.icia.delivery.global.response.ApiResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeservice;
    private final HttpSession session;
    private final ReviewService rsvc;

    @PostMapping("/details")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStoreDetails(@RequestParam Long storeId) {
        return ResponseEntity.ok(ApiResponse.success(storeservice.getStoreDetails(storeId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<List<PreStoreDTO>>> getAllStores() {
        List<PreStoreDTO> storeList = storeservice.getAllStores();
        return ResponseEntity.ok(ApiResponse.success(storeList));
    }

    @PostMapping("/category")
    public ResponseEntity<ApiResponse<List<PreStoreDTO>>> getStoresByCategory(@RequestParam String category) {
        List<PreStoreDTO> storeList = storeservice.getStoresByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(storeList));
    }

    @PostMapping("/{storeId}")
    public ResponseEntity<ApiResponse<List<PreStoreDTO>>> getstoresBystoreId(@PathVariable("storeId") Long storeId) {
        List<PreStoreDTO> storeList = storeservice.getstoresBystoreId(storeId);
        session.setAttribute("preStoId", storeId);
        return ResponseEntity.ok(ApiResponse.success(storeList));
    }
    @PostMapping("/reviewStarCount")
    public ResponseEntity<ApiResponse<Float>> reviewStarCount(@RequestParam("storeId") Long storeId){
        return ResponseEntity.ok(ApiResponse.success(rsvc.reviewStarCount(storeId)));
    }

    @PostMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics() {
        Map<String, Object> response = storeservice.getStatistics();
        System.out.println("리수폰 : "  +response);

        // 오늘의 주문 수 (단일 값으로 처리)
        Integer todayOrders = (Integer) response.get("todayOrders");
        Float starCount = (Float) response.get("starCount");
        List<Integer> starRatings = (List<Integer>) response.get("starRatings");

        // 지난 일주일 동안의 주문 수 (일주일 간의 주문 수를 날짜별로 나누어 포함)
        List<Integer> weekOrders = (List<Integer>) response.get("weekOrders");

        // 클라이언트에 전달할 데이터
        Map<String, Object> data = new HashMap<>();
        data.put("todayOrders", todayOrders);   // 오늘의 주문 수
        data.put("starCount", starCount);       // 리뷰 평균 별점
        data.put("starRatings", starRatings);   // 별점별 개수
        data.put("weekOrders", weekOrders);     // 지난 일주일 동안의 주문 수

        return ResponseEntity.ok(ApiResponse.success(data));
    }
    @PostMapping("/check")
    public ResponseEntity<ApiResponse<String>> checkStoretime() {
        // 서비스에서 가게의 운영 상태를 업데이트하고, 해당 상태("승인" 또는 "차단")를 반환받음
        String storeStatus = storeservice.checkStoretime();
        return ResponseEntity.ok(ApiResponse.success(storeStatus));
    }


    @PostMapping("/check/{preStoId}")
    public ResponseEntity<ApiResponse<String>> checkStoreStatus(@PathVariable("preStoId") Long preStoId) {
        // 로직
        String storeStatus = storeservice.checkStoreStatus(preStoId);
        return ResponseEntity.ok(ApiResponse.success(storeStatus));
    }

    @PostMapping("/storeName/{preStoId}")
    public ResponseEntity<ApiResponse<String>> getStoreNamebystoreId(@PathVariable("preStoId") Long preStoId){
        String storeName = storeservice.getStoreNamebystoreId(preStoId);
        return ResponseEntity.ok(ApiResponse.success(storeName));
    }

    @PostMapping("/info/{preStoId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStoreInfo(@PathVariable("preStoId") Long preStoId) {
        Map<String, Object> dto = storeservice.getStoreInfo(preStoId);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }
}
