package com.icia.delivery.domain.admin.controller;

import com.icia.delivery.domain.store.dto.PreStoreDTO;
import com.icia.delivery.domain.admin.service.DashboardService;
import com.icia.delivery.domain.admin.service.MarketingService;
import com.icia.delivery.domain.admin.service.SalesService;
import com.icia.delivery.global.response.ApiResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DashBoardChartController {

    private static final Logger log = LoggerFactory.getLogger(DashBoardChartController.class);

    private final DashboardService dashboardService;
    private final SalesService salesService;
    private final MarketingService marketingService;

    private final HttpSession session;

    // dashboardChart
    @PostMapping("/dashboardChart")
    public ResponseEntity<ApiResponse<Map<String, Long>>> dashboardChart() {
        // Service에서 카테고리별 주문 수를 집계한 Map을 받아옴
        Map<String, Long> categoryCountMap = dashboardService.dashboardChart();
        return ResponseEntity.ok(ApiResponse.success(categoryCountMap));
    }

    // salesChart
    @PostMapping("/salesChart")
    public ResponseEntity<ApiResponse<Map<String, Long>>> salesChart() {
        // Service에서 지난 7일 동안의 매출 금액을 집계한 Map을 받아옴
        Map<String, Long> salesData = salesService.getSalesForLastWeek();
        return ResponseEntity.ok(ApiResponse.success(salesData));
    }

    // topSellingStore
    @PostMapping("/topSellingStore")
    public ResponseEntity<ApiResponse<Map<Double, PreStoreDTO>>> topSellingStore() {
        // Service에서 매장별 매출 집계를 Map 형식으로 받아옴
        Map<Double, PreStoreDTO> topStore = salesService.topSellingStore();
        return ResponseEntity.ok(ApiResponse.success(topStore));
    }

    @PostMapping("/statisticsDateSel")
    public ResponseEntity<ApiResponse<Map<String, String>>> statisticsDateSel(@RequestBody Map<String, String> payload) {
        String date = payload.get("date");
        log.debug("Statistics date selected. date={}", date);
        Map<String, String> response = new HashMap<>();
        response.put("date", date);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // genderRatio_1
    @PostMapping("/genderRatio")
    public ResponseEntity<ApiResponse<Map<String, Double>>> genderRatio(HttpSession session){
        // 세션에서 개별 값 가져오기
        Double maleRatio = (Double) session.getAttribute("maleRatio");
        Double femaleRatio = (Double) session.getAttribute("femaleRatio");
        Double orderMaleRatio = (Double) session.getAttribute("orderMaleRatio");
        Double orderFemaleRatio = (Double) session.getAttribute("orderFemaleRatio");
        Double localLoginRatio = (Double) session.getAttribute("localLoginRatio");
        Double naverLoginRatio = (Double) session.getAttribute("naverLoginRatio");
        Double kakaoLoginRatio = (Double) session.getAttribute("kakaoLoginRatio");
        Double googleLoginRatio = (Double) session.getAttribute("googleLoginRatio");

        // 반환할 Map에 담기
        Map<String, Double> genderRatio = new HashMap<>();
        genderRatio.put("maleRatio", maleRatio);
        genderRatio.put("femaleRatio", femaleRatio);
        genderRatio.put("orderMaleRatio", orderMaleRatio);
        genderRatio.put("orderFemaleRatio", orderFemaleRatio);
        genderRatio.put("localLogin", localLoginRatio);
        genderRatio.put("naverLogin", naverLoginRatio);
        genderRatio.put("kakaoLogin", kakaoLoginRatio);
        genderRatio.put("googleLogin", googleLoginRatio);

        return ResponseEntity.ok(ApiResponse.success(genderRatio));
    }

    @PostMapping("/birthCategoryRatio")
    public ResponseEntity<ApiResponse<Map<String, Map<String, Integer>>>> birthCategoryRatio(){
        // Service에서 매장별 매출 집계를 Map 형식으로 받아옴
        Map<String, Map<String, Integer>> sss = marketingService.birthCategoryRatio();
        return ResponseEntity.ok(ApiResponse.success(sss));
    }

    @PostMapping("/topOrderMemList")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> topOrderMemList() {
        List<Map<String, Object>> topOM = marketingService.topOrderMemList();
        return ResponseEntity.ok(ApiResponse.success(topOM));
    }

    @PostMapping("/rewardGradeData")
    public ResponseEntity<ApiResponse<Map<String, Long>>> rewardGradeData() {
        Map<String, Long> reward = marketingService.rewardGradeData();
        return ResponseEntity.ok(ApiResponse.success(reward));
    }



}
