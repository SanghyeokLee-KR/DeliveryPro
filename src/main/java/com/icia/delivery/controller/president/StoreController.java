package com.icia.delivery.controller.president;

import com.icia.delivery.dto.president.PreStoreDTO;
import com.icia.delivery.service.member.ReviewService;
import com.icia.delivery.service.president.StoreService;
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
    public Map<String, Object> getStoreDetails(@RequestParam Long storeId) {
        return storeservice.getStoreDetails(storeId);
    }

    @PostMapping
    public ResponseEntity<List<PreStoreDTO>> getAllStores() {
        List<PreStoreDTO> storeList = storeservice.getAllStores();
        return ResponseEntity.ok(storeList);
    }

    @PostMapping("/category")
    public ResponseEntity<List<PreStoreDTO>> getStoresByCategory(@RequestParam String category) {
        List<PreStoreDTO> storeList = storeservice.getStoresByCategory(category);
        return ResponseEntity.ok(storeList);
    }

    @PostMapping("/{storeId}")
    public ResponseEntity<List<PreStoreDTO>> getstoresBystoreId(@RequestParam Long storeId) {
        List<PreStoreDTO> storeList = storeservice.getstoresBystoreId(storeId);
        session.setAttribute("preStoId", storeId);
        return ResponseEntity.ok(storeList);
    }
    @PostMapping("/reviewStarCount")
    public Float reviewStarCount(@RequestParam("storeId") Long storeId){
        return rsvc.reviewStarCount(storeId);
    }

    @PostMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
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

        return ResponseEntity.ok(data);
    }
    @PostMapping("/check")
    public ResponseEntity<String> checkStoretime() {
        // 서비스에서 가게의 운영 상태를 업데이트하고, 해당 상태("승인" 또는 "차단")를 반환받음
        String storeStatus = storeservice.checkStoretime();
        return ResponseEntity.ok(storeStatus);
    }


    @PostMapping("/check/{preStoId}")
    public ResponseEntity<String> checkStoreStatus(@PathVariable("preStoId") Long preStoId) {
        // 로직
        String storeStatus = storeservice.checkStoreStatus(preStoId);
        return ResponseEntity.ok(storeStatus);
    }

    @PostMapping("/storeName/{preStoId}")
    public ResponseEntity<String> getStoreNamebystoreId(@PathVariable("preStoId") Long preStoId){
        String storeName = storeservice.getStoreNamebystoreId(preStoId);
        return ResponseEntity.ok(storeName);
    }

    @PostMapping("/info/{preStoId}")
    public ResponseEntity<Map<String, Object>> getStoreInfo(@PathVariable("preStoId") Long preStoId) {
        Map<String, Object> dto = storeservice.getStoreInfo(preStoId);
        return ResponseEntity.ok(dto);
    }
}
